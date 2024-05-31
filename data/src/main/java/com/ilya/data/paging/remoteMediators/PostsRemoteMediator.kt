package com.ilya.data.paging.remoteMediators

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.ilya.core.appCommon.AccessTokenManager
import com.ilya.core.appCommon.BaseFactory
import com.ilya.core.util.logThrowable
import com.ilya.data.local.LocalRepository
import com.ilya.data.local.database.PostEntity
import com.ilya.data.network.UserDataRemoteRepository
import com.ilya.data.network.retrofit.CURRENT_API_VERSION
import com.ilya.data.network.retrofit.api.PostDto
import com.ilya.data.network.retrofit.api.UserDto
import com.ilya.data.network.retrofit.api.VideoDto
import com.ilya.data.network.retrofit.api.VideoExtendedDataDto
import com.ilya.data.paging.PaginationError
import com.ilya.data.toPostEntity
import com.squareup.moshi.Json
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.POST
import retrofit2.http.Query
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class PostsRemoteMediator(
    private val localRepository: LocalRepository<PostEntity>,
    private val remoteRepository: UserDataRemoteRepository,
    private val accessTokenManager: AccessTokenManager,
    private val userId: Long,
    retrofit: Retrofit
) : RemoteMediator<Int, PostEntity>() {

    private val vkApiExecutor = retrofit.create<VkApiExecutor>()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>
    ): MediatorResult {
        try {
            val offset = when (loadType) {
                LoadType.REFRESH -> 0
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    state.lastItemOrNull()?.databaseId ?: return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                }
            }

            val count = when (loadType) {
                LoadType.REFRESH -> state.config.initialLoadSize
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> state.config.pageSize
            }

            val accessToken = accessTokenManager.accessToken ?: return MediatorResult.Error(
                PaginationError.NoAccessToken
            )

            val posts = remoteRepository.getWall(
                accessToken = accessToken.token,
                ownerId = userId,
                count = count,
                offset = offset
            )

            val postsAdditionalData = getPostsAdditionalData(posts, accessToken.token)

            localRepository.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    localRepository.deleteAllWithPrimaryKeys()
                }
                val postEntities = posts.map { post ->
                    val videos = postsAdditionalData[post.id]
                        ?.response
                        ?.videos
                        ?.items ?: emptyList()
                    val postOwner = postsAdditionalData[post.id]
                        ?.response
                        ?.postOwner
                        ?.data ?: UserDto()
                    post.toPostEntity(videos, postOwner)
                }
                localRepository.upsertAll(*postEntities.toTypedArray())
            }

            return MediatorResult.Success(endOfPaginationReached = posts.isEmpty())

        } catch (e: SocketTimeoutException) {
            logThrowable(e)
            return MediatorResult.Error(PaginationError.NoInternet)
        } catch (e: UnknownHostException) {
            logThrowable(e)
            return MediatorResult.Error(PaginationError.NoInternet)
        } catch (e: Exception) {
            logThrowable(e)
            return MediatorResult.Error(e)
        }
    }

    private fun extractVideoIds(posts: List<PostDto>): Map<Long, List<String>> {
        return posts.associate { post ->
            val videoIds = mutableListOf<String>()

            for (attachment in post.attachments) {
                if (attachment.type == VIDEO) {
                    attachment.video ?: continue

                    videoIds += combineFullVideoId(attachment.video)
                }
            }

            post.id to videoIds
        }.filter { entry -> entry.value.isNotEmpty() }
    }

    private fun combineFullVideoId(video: VideoDto): String {
        val videoId = mutableListOf(video.ownerId.toString(), video.id.toString())
        if (video.accessKey.isNotEmpty()) {
            videoId += video.accessKey
        }
        return videoId.joinToString(separator = "_")
    }

    private suspend fun getPostsAdditionalData(
        posts: List<PostDto>,
        accessToken: String
    ): Map<Long, AdditionalPostDataResponse> {
        val postVideoIds = extractVideoIds(posts)

        return posts.associate { post ->
            val vkScriptRequest = """ 
                ${
                if (postVideoIds[post.id] != null) {
                    """
                            var videoIds = [${postVideoIds[post.id]?.joinToString(",") ?: ""}];
                            var videos = API.video.get({"videos": videoIds});
                        """.trimIndent()
                } else {
                    ""
                }
            }
                var owner = API.users.get({"user_ids": [${post.ownerId}], "fields": ["photo_200_orig"]});
                    
                return {
                    ${
                if (postVideoIds[post.id] != null) {
                    """
                                    "videos": {
                                        "post_id": ${post.id}
                                        "items": videos.items
                                    },
                                """.trimIndent()
                } else {
                    ""
                }
            }
                    "post_owner": {
                        "post_id": ${post.id},
                        "data": owner[0]
                    }
                };
            """.trimIndent()
            post.id to vkApiExecutor.execute(
                accessToken = accessToken,
                code = vkScriptRequest
            )
        }
    }

    class Factory @Inject constructor(
        private val localRepository: LocalRepository<PostEntity>,
        private val remoteRepository: UserDataRemoteRepository,
        private val accessTokenManager: AccessTokenManager,
        private val retrofit: Retrofit
    ) : BaseFactory<Long, PostsRemoteMediator> {
        override fun newInstance(initializationData: Long): PostsRemoteMediator {
            return PostsRemoteMediator(
                localRepository,
                remoteRepository,
                accessTokenManager,
                initializationData,
                retrofit
            )
        }
    }

    companion object {
        const val VIDEO = "video"
    }

}

private interface VkApiExecutor {

    @POST("execute?v=$CURRENT_API_VERSION")
    suspend fun execute(
        @Query("access_token") accessToken: String,
        @Query("code") code: String
    ): AdditionalPostDataResponse

}


private data class AdditionalPostDataResponse(
    @Json(name = "response") val response: AdditionalPostData
)

private data class AdditionalPostData(
    @Json(name = "videos") val videos: AdditionalVideosDataDto? = null,
    @Json(name = "post_owner") val postOwner: AdditionalPostOwnerDto
)

private data class AdditionalVideosDataDto(
    @Json(name = "post_id") val postId: Long,
    @Json(name = "items") val items: List<VideoExtendedDataDto>
)

private data class AdditionalPostOwnerDto(
    @Json(name = "post_id") val postId: Long,
    @Json(name = "data") val data: UserDto
)
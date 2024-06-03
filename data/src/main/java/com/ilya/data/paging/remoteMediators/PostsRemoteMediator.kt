package com.ilya.data.paging.remoteMediators

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.ilya.core.appCommon.AccessTokenManager
import com.ilya.core.appCommon.BaseFactory
import com.ilya.core.appCommon.enums.AttachmentType
import com.ilya.core.util.logThrowable
import com.ilya.data.local.LocalRepository
import com.ilya.data.local.database.PostEntity
import com.ilya.data.network.UserDataRemoteRepository
import com.ilya.data.network.VkApiExecutor
import com.ilya.data.network.retrofit.api.AdditionalPostData
import com.ilya.data.network.retrofit.api.AttachmentDto
import com.ilya.data.network.retrofit.api.BaseAttachment
import com.ilya.data.network.retrofit.api.PostDto
import com.ilya.data.network.retrofit.api.UserDto
import com.ilya.data.paging.PaginationError
import com.ilya.data.toPostEntity
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class PostsRemoteMediator(
    private val localRepository: LocalRepository<PostEntity>,
    private val remoteRepository: UserDataRemoteRepository,
    private val accessTokenManager: AccessTokenManager,
    private val userId: Long,
    private val vkApiExecutor: VkApiExecutor<AdditionalPostData>
) : RemoteMediator<Int, PostEntity>() {

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

            val databasePosts = localRepository.getAll()

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
                        ?.videos
                        ?.items ?: emptyList()
                    val postOwner = postsAdditionalData[post.id]
                        ?.postOwner
                        ?.data ?: UserDto()
                    val photos = postsAdditionalData[post.id]
                        ?.photos
                        ?.items ?: emptyList()
                    post.toPostEntity(videos, photos, postOwner)
                }.filterNot {
                    databasePosts.map { databasePost ->
                        databasePost.copy(databaseId = 0)
                    }.contains(it.copy(databaseId = 0))
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

    private fun <T> extractIds(
        posts: List<PostDto>,
        attachmentType: AttachmentType,
        getId: (AttachmentDto) -> T?
    ): Map<Long, List<T>> {
        return posts.associate { post ->
            val ids = mutableListOf<T>()

            for (attachment in post.attachments) {
                if (attachment.type == attachmentType.value) {
                    val id = getId(attachment)
                    id ?: continue
                    ids += id
                }
            }

            post.id to ids
        }.filter { entry -> entry.value.isNotEmpty() }
    }

    private fun <T : BaseAttachment> combineFullId(attachment: T): String {
        val id = mutableListOf(attachment.ownerId.toString(), attachment.id.toString())
        if (attachment.accessKey.isNotEmpty()) {
            id += attachment.accessKey
        }
        return id.joinToString("_")
    }

    private suspend fun getPostsAdditionalData(
        posts: List<PostDto>,
        accessToken: String
    ): Map<Long, AdditionalPostData> {
        val postVideoIds = extractIds(
            posts = posts,
            attachmentType = AttachmentType.VIDEO,
            getId = { it.video?.let { video -> combineFullId(video) } }
        )
        val postPhotoIds = extractIds(
            posts = posts,
            attachmentType = AttachmentType.PHOTO,
            getId = { it.photo?.let { photo -> combineFullId(photo) } }
        )

        return posts.associate { post ->
            val vkScriptRequest = """ 
            ${
                if (postVideoIds[post.id] != null) {
                    """
                        var videoIds = [${postVideoIds[post.id]?.joinToString(",") { "\"$it\"" }}];
                        var videos = API.video.get({"videos": videoIds});
                    """.trimIndent()
                } else {
                    ""
                }
            }
            ${
                if (postPhotoIds[post.id] != null) {
                    """
                        var photoIds = [${postPhotoIds[post.id]?.joinToString(",") { "\"$it\"" }}];
                        var photos = API.photos.getById({
                            "photos": photoIds,
                            "extended": 1
                        });                       
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
                            "post_id": ${post.id},
                            "items": videos.items
                        },
                    """.trimIndent()
                } else {
                    ""
                }
            }
            ${
                if (postPhotoIds[post.id] != null) {
                    """
                        "photos": {
                            "post_id": ${post.id},
                            "items": photos
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
        private val vkApiExecutor: VkApiExecutor<AdditionalPostData>
    ) : BaseFactory<Long, PostsRemoteMediator> {
        override fun newInstance(initializationData: Long): PostsRemoteMediator {
            return PostsRemoteMediator(
                localRepository,
                remoteRepository,
                accessTokenManager,
                initializationData,
                vkApiExecutor
            )
        }
    }

}

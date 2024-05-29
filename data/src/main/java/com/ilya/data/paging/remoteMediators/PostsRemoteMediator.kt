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
import com.ilya.data.network.retrofit.api.PostDto
import com.ilya.data.network.retrofit.api.UserDto
import com.ilya.data.network.retrofit.api.VideoDto
import com.ilya.data.network.retrofit.api.VideoExtendedDataDto
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
    private val userId: Long
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

            val posts = remoteRepository.getWall(
                accessToken = accessToken.token,
                ownerId = userId,
                count = count,
                offset = offset
            )

            val postVideoAttachments = extractVideos(posts, accessToken.token)
            val postsOwners = extractPostOwners(posts, accessToken.token)

            localRepository.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    localRepository.deleteAllWithPrimaryKeys()
                }
                val postEntities = posts.map { post ->
                    val videos = postVideoAttachments[post.id] ?: emptyList()
                    val postOwner = postsOwners[post.id] ?: UserDto()
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

    private suspend fun extractPostOwners(
        posts: List<PostDto>,
        accessToken: String
    ): Map<Long, UserDto> {
        return posts.associate { post ->
            post.id to remoteRepository.getUser(
                accessToken = accessToken,
                userId = post.ownerId,
                fields = POST_OWNER_FIELDS
            )
        }
    }

    private suspend fun extractVideos(
        posts: List<PostDto>,
        accessToken: String
    ): Map<Long, List<VideoExtendedDataDto>> {
        return posts.associate { post ->
            val videos = mutableListOf<VideoExtendedDataDto>()

            for (attachment in post.attachments) {
                if (attachment.type == VIDEO) {
                    attachment.video ?: continue

                    val video = remoteRepository.getVideoData(
                        accessToken = accessToken,
                        ownerId = attachment.video.ownerId,
                        videoId = combineFullVideoId(attachment.video)
                    )
                    videos += video
                }
            }

            post.id to videos
        }
    }


    private fun combineFullVideoId(video: VideoDto): String {
        val videoId = mutableListOf(video.ownerId.toString(), video.id.toString())
        if (video.accessKey.isNotEmpty()) {
            videoId += video.accessKey
        }
        return videoId.joinToString(separator = "_")
    }

    class Factory @Inject constructor(
        private val localRepository: LocalRepository<PostEntity>,
        private val remoteRepository: UserDataRemoteRepository,
        private val accessTokenManager: AccessTokenManager
    ) : BaseFactory<Long, PostsRemoteMediator> {
        override fun newInstance(initializationData: Long): PostsRemoteMediator {
            return PostsRemoteMediator(
                localRepository,
                remoteRepository,
                accessTokenManager,
                initializationData
            )
        }
    }

    companion object {
        const val VIDEO = "video"
        private val POST_OWNER_FIELDS = listOf("photo_200_orig")
    }

}
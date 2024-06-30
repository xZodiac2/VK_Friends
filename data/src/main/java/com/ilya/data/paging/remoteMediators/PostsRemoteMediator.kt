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
import com.ilya.data.local.database.entities.PostWithAttachmentsAndOwner
import com.ilya.data.remote.UserDataRemoteRepository
import com.ilya.data.remote.VkApiExecutor
import com.ilya.data.paging.PaginationError
import com.ilya.data.remote.retrofit.api.dto.AdditionalPostData
import com.ilya.data.remote.retrofit.api.dto.AttachmentDto
import com.ilya.data.remote.retrofit.api.dto.BaseAttachment
import com.ilya.data.remote.retrofit.api.dto.PostDto
import com.ilya.data.remote.retrofit.api.dto.UserDto
import com.ilya.data.mappers.toPostEntity
import kotlinx.coroutines.delay
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class PostsRemoteMediator private constructor(
    private val localRepository: LocalRepository<PostWithAttachmentsAndOwner>,
    private val remoteRepository: UserDataRemoteRepository,
    private val accessTokenManager: AccessTokenManager,
    private val userId: Long,
    private val vkApiExecutor: VkApiExecutor<AdditionalPostData>
) : RemoteMediator<Int, PostWithAttachmentsAndOwner>() {

    // delays in this method are necessary because of requests limit 3/sec
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostWithAttachmentsAndOwner>
    ): MediatorResult {
        try {
            if (loadType == LoadType.REFRESH) {
                localRepository.deleteAllWithPrimaryKeys()
            }

            val offset = when (loadType) {
                LoadType.REFRESH -> 0
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    state.lastItemOrNull()?.data?.pagingId ?: return MediatorResult.Success(
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

            val databasePostIds = localRepository.getAll().map { it.data.id }

            delay(500)
            val posts = remoteRepository.getWall(
                accessToken = accessToken.token,
                ownerId = userId,
                count = count,
                offset = offset
            )

            val postsAdditionalData = getPostsAdditionalData(posts, accessToken.token)

            val postEntities = posts
                .map { post ->
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
                }
                .filterNot { databasePostIds.contains(it.data.id) }
            localRepository.upsertAll(*postEntities.toTypedArray())

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
            delay(500)
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
                var owner = API.users.get({"user_ids": [${post.authorId}], "fields": ["photo_200_orig"]});
                    
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
        private val localRepository: LocalRepository<PostWithAttachmentsAndOwner>,
        private val remoteRepository: UserDataRemoteRepository,
        private val accessTokenManager: AccessTokenManager,
        private val vkApiExecutor: VkApiExecutor<AdditionalPostData>
    ) : BaseFactory<Long, PostsRemoteMediator> {
        override fun newInstance(initializationData: Long): PostsRemoteMediator {
            return PostsRemoteMediator(
                localRepository = localRepository,
                remoteRepository = remoteRepository,
                accessTokenManager = accessTokenManager,
                userId = initializationData,
                vkApiExecutor = vkApiExecutor
            )
        }
    }

}

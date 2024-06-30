package com.ilya.data.paging.pagingSources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ilya.core.appCommon.AccessTokenManager
import com.ilya.core.appCommon.BaseFactory
import com.ilya.core.appCommon.enums.AttachmentType
import com.ilya.core.util.logThrowable
import com.ilya.data.mappers.toPost
import com.ilya.data.paging.PaginationError
import com.ilya.data.paging.Post
import com.ilya.data.remote.UserDataRemoteRepository
import com.ilya.data.remote.VkApiExecutor
import com.ilya.data.remote.retrofit.api.dto.AdditionalPostData
import com.ilya.data.remote.retrofit.api.dto.AttachmentDto
import com.ilya.data.remote.retrofit.api.dto.BaseAttachment
import com.ilya.data.remote.retrofit.api.dto.PostDto
import com.ilya.data.remote.retrofit.api.dto.UserDto
import kotlinx.coroutines.delay
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class PostsPagingSource private constructor(
    private val vkApiExecutor: VkApiExecutor<AdditionalPostData>,
    private val userDataRemoteRepository: UserDataRemoteRepository,
    private val accessTokenManager: AccessTokenManager,
    private val userId: Long
) : PagingSource<Int, Post>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> {
        try {
            val key = params.key ?: 0
            val offset = key * params.loadSize

            val accessToken = accessTokenManager.accessToken?.token ?: return LoadResult.Error(
                PaginationError.NoAccessToken
            )

            val wall = userDataRemoteRepository.getWall(
                accessToken = accessToken,
                ownerId = userId,
                count = params.loadSize,
                offset = offset
            )

            val postsAdditionalData = getPostsAdditionalData(wall, accessToken)

            val posts = wall.map { post ->
                val videos = postsAdditionalData[post.id]
                    ?.videos
                    ?.items ?: emptyList()
                val postOwner = postsAdditionalData[post.id]
                    ?.postOwner
                    ?.data ?: UserDto()
                val photos = postsAdditionalData[post.id]
                    ?.photos
                    ?.items ?: emptyList()
                post.toPost(videos, photos, postOwner)
            }

            return LoadResult.Page(
                data = posts,
                nextKey = if (posts.isEmpty()) null else key + 1,
                prevKey = null,
            )

        } catch (e: SocketTimeoutException) {
            logThrowable(e)
            return LoadResult.Error(PaginationError.NoInternet)
        } catch (e: UnknownHostException) {
            logThrowable(e)
            return LoadResult.Error(PaginationError.NoInternet)
        } catch (e: Exception) {
            logThrowable(e)
            return LoadResult.Error(e)
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

    override fun getRefreshKey(state: PagingState<Int, Post>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    class Factory @Inject constructor(
        private val vkApiExecutor: VkApiExecutor<AdditionalPostData>,
        private val userDataRemoteRepository: UserDataRemoteRepository,
        private val accessTokenManager: AccessTokenManager,
    ) : BaseFactory<Long, PostsPagingSource> {
        override fun newInstance(initializationData: Long): PostsPagingSource {
            return PostsPagingSource(
                vkApiExecutor = vkApiExecutor,
                userDataRemoteRepository = userDataRemoteRepository,
                accessTokenManager = accessTokenManager,
                userId = initializationData
            )
        }

    }

}

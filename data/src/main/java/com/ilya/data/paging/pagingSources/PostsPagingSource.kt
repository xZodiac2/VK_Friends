package com.ilya.data.paging.pagingSources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ilya.core.appCommon.AccessTokenManager
import com.ilya.core.appCommon.BaseFactory
import com.ilya.core.appCommon.enums.AttachmentType
import com.ilya.core.util.logThrowable
import com.ilya.data.mappers.toPost
import com.ilya.data.mappers.toRepostedPost
import com.ilya.data.paging.PaginationError
import com.ilya.data.paging.Post
import com.ilya.data.remote.UserDataRemoteRepository
import com.ilya.data.remote.VkApiExecutor
import com.ilya.data.remote.retrofit.api.dto.AdditionalPostData
import com.ilya.data.remote.retrofit.api.dto.AttachmentDto
import com.ilya.data.remote.retrofit.api.dto.BaseAttachment
import com.ilya.data.remote.retrofit.api.dto.HistoryPostDto
import com.ilya.data.remote.retrofit.api.dto.LikesDto
import com.ilya.data.remote.retrofit.api.dto.PostDto
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

            val reposted = wall.mapNotNull { it.cotyHistory.firstOrNull() }
            val ids = wall.mapNotNull {
                if (it.cotyHistory.isEmpty()) {
                    null
                } else {
                    it.cotyHistory.first().id to it.id
                }
            }.toMap()
            val repostedAdditionalData = getPostsAdditionalData(
                posts = reposted.map { it.toPostDto() },
                accessToken = accessToken
            )

            val repostedPosts = reposted.associate { post ->
                val videos = repostedAdditionalData[post.id]
                    ?.videos
                    ?.items ?: emptyList()
                val postOwner = repostedAdditionalData[post.id]
                    ?.postOwner
                    ?.data
                val group = repostedAdditionalData[post.id]
                    ?.postOwner
                    ?.group
                val photos = repostedAdditionalData[post.id]
                    ?.photos
                    ?.items ?: emptyList()
                val ownerPostId = ids[post.id] ?: 0
                ownerPostId to post.toRepostedPost(videos, photos, postOwner, group)
            }

            val posts = wall.mapNotNull { post ->
                val videos = postsAdditionalData[post.id]
                    ?.videos
                    ?.items ?: emptyList()
                val postOwner = postsAdditionalData[post.id]
                    ?.postOwner
                    ?.data
                val photos = postsAdditionalData[post.id]
                    ?.photos
                    ?.items ?: emptyList()
                val repostedPost = repostedPosts[post.id]
                postOwner?.let { post.toPost(videos, photos, it, repostedPost) }
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

    private fun HistoryPostDto.toPostDto(): PostDto {
        val fakeLikes = LikesDto(0, 0)
        val fakeDate = 0L
        val fakeOwnerId = 0L

        return PostDto(
            id = id,
            text = text,
            attachments = attachments,
            likes = fakeLikes,
            dateUnixTime = fakeDate,
            authorId = authorId,
            ownerId = fakeOwnerId
        )
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
            val photoAccessKeys = postPhotoIds[post.id]?.associate {
                it
                    .substringAfter("_")
                    .substringBefore("_")
                    .toLong() to it.substringAfterLast("_")
            }
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
            ${
                if (post.authorId < 0) {
                    """
                        var owner = API.groups.getById({"group_id": -1 * ${post.authorId}});
                    """.trimIndent()
                } else {
                    """
                        var owner = API.users.get({
                            "user_ids": [${post.authorId}],
                            "fields": ["photo_200_orig"]
                        });
                    """.trimIndent()
                }
            }
                
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
                        "user": owner[0],
                        "group": owner.groups[0],
                        "posted_by_group": ${post.authorId} < 0
                    }
                };
            """.trimIndent()
            val response = vkApiExecutor.execute(
                accessToken = accessToken,
                code = vkScriptRequest
            )
            post.id to response.copy(
                photos = response.photos?.copy(
                    items = response.photos.items.map {
                        it.copy(accessKey = photoAccessKeys?.get(it.id) ?: "")
                    }
                )
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

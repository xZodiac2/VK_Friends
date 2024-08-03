package com.ilya.paging.pagingSources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ilya.core.appCommon.accessToken.AccessTokenManager
import com.ilya.core.appCommon.base.BaseFactory
import com.ilya.core.util.logThrowable
import com.ilya.data.WallRemoteRepository
import com.ilya.paging.PaginationError
import com.ilya.paging.mappers.toComment
import com.ilya.paging.mappers.toUser
import com.ilya.paging.models.Comment
import java.io.IOException
import javax.inject.Inject

class CommentsPagingSource private constructor(
    private val wallRepository: WallRemoteRepository,
    private val accessTokenManager: AccessTokenManager,
    private val ownerId: Long,
    private val postId: Long
) : PagingSource<Int, Comment>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Comment> {
        return try {
            val key = params.key ?: 0
            val offset = key * params.loadSize

            val accessToken = accessTokenManager.accessToken ?: return LoadResult.Error(PaginationError.NoAccessToken)

            val commentsResponse = wallRepository.getComments(
                accessToken = accessToken.token,
                ownerId = ownerId,
                postId = postId,
                needLikes = 1,
                offset = offset,
                count = params.loadSize,
                extended = 1,
                fields = FIELDS.joinToString(",")
            )

            val commentsDto = commentsResponse.comments
            val owners = commentsDto.mapNotNull { comment ->
                val owner = commentsResponse.profiles.find { it.id == comment.fromId }
                owner?.let { comment.id to it }
            }.toMap()

            val threads = commentsDto.associate { it.id to it.thread }

            val comments = commentsDto.map {
                it.toComment(
                    owner = owners[it.id]?.toUser(),
                    replyToUser = null,
                    thread = threads[it.id]?.comments?.map { thread ->
                        val replyToUser = commentsResponse.profiles.find { profile -> profile.id == thread.replyToUser }
                        val owner = commentsResponse.profiles.find { profile -> profile.id == thread.fromId }
                        thread.toComment(owner?.toUser(), replyToUser?.toUser(), thread = emptyList())
                    } ?: emptyList()
                )
            }

            LoadResult.Page(
                data = comments,
                nextKey = if (comments.isEmpty()) null else key + 1,
                prevKey = null
            )

        } catch (e: IOException) {
            logThrowable(e)
            LoadResult.Error(PaginationError.NoInternet)
        } catch (e: Exception) {
            logThrowable(e)
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Comment>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    class Factory @Inject constructor(
        private val wallRepository: WallRemoteRepository,
        private val accessTokenManager: AccessTokenManager
    ) : BaseFactory<InitData, CommentsPagingSource> {
        override fun newInstance(initializationData: InitData): CommentsPagingSource {
            return CommentsPagingSource(
                wallRepository = wallRepository,
                accessTokenManager = accessTokenManager,
                ownerId = initializationData.ownerId,
                postId = initializationData.postId
            )
        }
    }

    data class InitData(
        val ownerId: Long,
        val postId: Long
    )

    companion object {
        private val FIELDS = listOf("photo_200_orig")
    }

}
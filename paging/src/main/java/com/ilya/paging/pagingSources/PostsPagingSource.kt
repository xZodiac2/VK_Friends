package com.ilya.paging.pagingSources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ilya.core.appCommon.accessToken.AccessTokenManager
import com.ilya.core.appCommon.base.BaseFactory
import com.ilya.core.util.logThrowable
import com.ilya.data.GroupsRemoteRepository
import com.ilya.data.UserDataRemoteRepository
import com.ilya.data.WallRemoteRepository
import com.ilya.data.retrofit.api.dto.HistoryPostDto
import com.ilya.data.retrofit.api.dto.PostDto
import com.ilya.paging.PaginationError
import com.ilya.paging.mappers.toPost
import com.ilya.paging.mappers.toRepostedPost
import com.ilya.paging.models.Post
import com.ilya.paging.models.RepostedPost
import kotlinx.coroutines.delay
import java.io.IOException
import javax.inject.Inject

class PostsPagingSource private constructor(
    private val wallRemoteRepository: WallRemoteRepository,
    private val groupsRemoteRepository: GroupsRemoteRepository,
    private val userDataRemoteRepository: UserDataRemoteRepository,
    private val accessTokenManager: AccessTokenManager,
    private val userId: Long
) : PagingSource<Int, Post>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> {
        try {
            val key = params.key ?: 0
            val offset = key * params.loadSize

            val accessToken = accessTokenManager.accessToken ?: return LoadResult.Error(PaginationError.NoAccessToken)

            val wall = wallRemoteRepository.getWall(
                accessToken = accessToken.token,
                ownerId = userId,
                count = params.loadSize,
                offset = offset
            )

            val reposted = wall.mapNotNull { post ->
                post.cotyHistory.firstOrNull()?.let { post.id to it }
            }.toMap()

            val repostedWithOwners = reposted.injectPostOwners(accessToken.token)
            val postsWithOwners = wall.injectPostOwners(accessToken.token)

            val posts = postsWithOwners.map {
                it.copy(reposted = repostedWithOwners[it.id])
            }

            return LoadResult.Page(
                data = posts,
                nextKey = if (posts.isEmpty()) null else key + 1,
                prevKey = null,
            )

        } catch (e: IOException) {
            logThrowable(e)
            return LoadResult.Error(PaginationError.NoInternet)
        } catch (e: Exception) {
            logThrowable(e)
            return LoadResult.Error(e)
        }
    }

    private suspend fun List<PostDto>.injectPostOwners(accessToken: String): List<Post> {
        return this.map {
            delay(500)
            val owner = userDataRemoteRepository.getUser(
                accessToken = accessToken,
                userId = it.authorId,
                fields = listOf("photo_200_orig")
            )
            it.toPost(owner, null)
        }
    }

    private suspend fun Map<Long, HistoryPostDto>.injectPostOwners(accessToken: String): Map<Long, RepostedPost> {
        return this.map { (key, value) ->
            delay(500)
            if (value.authorId < 0) {
                val group = groupsRemoteRepository.getGroup(accessToken, -value.authorId)
                key to value.toRepostedPost(
                    owner = null,
                    group = group
                )
            } else {
                val owner = userDataRemoteRepository.getUser(
                    accessToken, value.authorId,
                    fields = listOf("photo_200_orig")
                )
                key to value.toRepostedPost(
                    owner = owner,
                    group = null
                )
            }
        }.toMap()
    }

    override fun getRefreshKey(state: PagingState<Int, Post>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    class Factory @Inject constructor(
        private val userDataRemoteRepository: UserDataRemoteRepository,
        private val accessTokenManager: AccessTokenManager,
        private val groupsRemoteRepository: GroupsRemoteRepository,
        private val wallRemoteRepository: WallRemoteRepository
    ) : BaseFactory<Long, PostsPagingSource> {
        override fun newInstance(initializationData: Long): PostsPagingSource {
            return PostsPagingSource(
                userDataRemoteRepository = userDataRemoteRepository,
                groupsRemoteRepository = groupsRemoteRepository,
                accessTokenManager = accessTokenManager,
                wallRemoteRepository = wallRemoteRepository,
                userId = initializationData
            )
        }
    }

}

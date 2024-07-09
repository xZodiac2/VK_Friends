package com.ilya.paging.pagingSources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ilya.core.appCommon.AccessTokenManager
import com.ilya.core.appCommon.BaseFactory
import com.ilya.core.util.logThrowable
import com.ilya.data.UsersRemoteRepository
import com.ilya.paging.PaginationError
import com.ilya.paging.User
import com.ilya.paging.mappers.toUser
import java.io.IOException
import javax.inject.Inject

class FriendsPagingSource private constructor(
    private val usersRemoteRepository: UsersRemoteRepository,
    private val accessTokenManager: AccessTokenManager
) : PagingSource<Int, User>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
        try {
            val key = params.key ?: 0
            val offset = key * params.loadSize

            val accessToken = accessTokenManager.accessToken?.token ?: return LoadResult.Error(
                PaginationError.NoAccessToken
            )

            val friends = usersRemoteRepository.getFriends(
                accessToken = accessToken,
                offset = offset,
                count = params.loadSize,
                fields = DEFAULT_FIELDS
            )

            return LoadResult.Page(
                data = friends.map { it.toUser() },
                nextKey = if (friends.isEmpty()) null else key + 1,
                prevKey = null
            )

        } catch (e: IOException) {
            logThrowable(e)
            return LoadResult.Error(PaginationError.NoInternet)
        } catch (e: Exception) {
            logThrowable(e)
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, User>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    class Factory @Inject constructor(
        private val usersRemoteRepository: UsersRemoteRepository,
        private val accessTokenManager: AccessTokenManager
    ) : BaseFactory<Unit, FriendsPagingSource> {
        override fun newInstance(initializationData: Unit): FriendsPagingSource {
            return FriendsPagingSource(
                usersRemoteRepository = usersRemoteRepository,
                accessTokenManager = accessTokenManager
            )
        }

    }

    companion object {
        private val DEFAULT_FIELDS = listOf("photo_200_orig")
    }

}
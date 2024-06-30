package com.ilya.data.paging.pagingSources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ilya.core.appCommon.AccessTokenManager
import com.ilya.core.util.logThrowable
import com.ilya.data.paging.PaginationError
import com.ilya.data.remote.UsersRemoteRepository
import com.ilya.data.remote.retrofit.api.dto.UserDto
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class FriendsPagingSource @Inject constructor(
    private val usersRemoteRepository: UsersRemoteRepository,
    private val accessTokenManager: AccessTokenManager
) : PagingSource<Int, UserDto>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserDto> {
        try {
            val key = params.key ?: 0
            val offset = key * params.loadSize

            val accessToken = accessTokenManager.accessToken?.token ?: return LoadResult.Error(
                PaginationError.NoAccessToken
            )

            val users = usersRemoteRepository.getFriends(
                accessToken = accessToken,
                offset = offset,
                count = params.loadSize,
                fields = DEFAULT_FIELDS
            )

            return LoadResult.Page(
                data = users,
                nextKey = if (users.isEmpty()) null else key + 1,
                prevKey = null
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

    override fun getRefreshKey(state: PagingState<Int, UserDto>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    companion object {
        private val DEFAULT_FIELDS = listOf("photo_200_orig")
    }

}
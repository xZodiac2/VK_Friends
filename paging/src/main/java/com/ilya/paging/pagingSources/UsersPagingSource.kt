package com.ilya.paging.pagingSources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ilya.core.appCommon.AccessTokenManager
import com.ilya.core.appCommon.BaseFactory
import com.ilya.core.util.logThrowable
import com.ilya.data.UsersRemoteRepository
import com.ilya.data.retrofit.api.dto.UserDto
import com.ilya.paging.PaginationError
import com.ilya.paging.User
import com.ilya.paging.mappers.toUser
import java.io.IOException
import javax.inject.Inject

class UsersPagingSource private constructor(
    private val accessTokenManager: AccessTokenManager,
    private val usersRemoteRepository: UsersRemoteRepository,
    private val query: String
) : PagingSource<Int, User>() {

    override val keyReuseSupported: Boolean = true
    private var initialLoad = true

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
        try {
            val key = params.key ?: 0
            if (key == 0 && initialLoad) {
                initialLoad = false
                return LoadResult.Page(
                    data = emptyList(),
                    nextKey = 0,
                    prevKey = null,
                    itemsAfter = 1
                )
            }

            val offset = key * params.loadSize

            val accessToken = accessTokenManager.accessToken?.token ?: return LoadResult.Error(
                PaginationError.NoAccessToken
            )

            val users = if (query.isEmpty()) {
                val response = getSuggestions(accessToken, params.loadSize, offset)
                response.ifEmpty { searchUsers(accessToken, params.loadSize, offset) }
            } else {
                searchUsers(accessToken, params.loadSize, offset)
            }

            return LoadResult.Page(
                data = users.map { it.toUser() },
                nextKey = if (users.isEmpty()) null else key + 1,
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

    private suspend fun searchUsers(token: String, count: Int, offset: Int): List<UserDto> {
        return usersRemoteRepository.searchUsers(
            accessToken = token,
            count = count,
            offset = offset,
            fields = DEFAULT_FIELDS,
            query = query
        )
    }

    private suspend fun getSuggestions(token: String, count: Int, offset: Int): List<UserDto> {
        return usersRemoteRepository.getSuggestions(
            accessToken = token,
            count = count,
            offset = offset,
            fields = DEFAULT_FIELDS,
        )
    }

    override fun getRefreshKey(state: PagingState<Int, User>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    class Factory @Inject constructor(
        private val accessTokenManager: AccessTokenManager,
        private val usersRemoteRepository: UsersRemoteRepository,
    ) : BaseFactory<String, UsersPagingSource> {
        override fun newInstance(initializationData: String): UsersPagingSource {
            return UsersPagingSource(
                accessTokenManager = accessTokenManager,
                usersRemoteRepository = usersRemoteRepository,
                query = initializationData
            )
        }
    }

    companion object {
        private val DEFAULT_FIELDS = listOf("photo_200_orig")
    }

}
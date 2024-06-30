package com.ilya.data.paging.remoteMediators

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.ilya.core.appCommon.AccessTokenManager
import com.ilya.core.appCommon.BaseFactory
import com.ilya.core.util.logThrowable
import com.ilya.data.local.LocalRepository
import com.ilya.data.local.database.entities.UserPagingEntity
import com.ilya.data.remote.UsersRemoteRepository
import com.ilya.data.paging.PaginationError
import com.ilya.data.remote.retrofit.api.dto.UserDto
import com.ilya.data.mappers.toUserEntity
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class UsersRemoteMediator private constructor(
    private val remoteRepo: UsersRemoteRepository,
    private val localRepository: LocalRepository<UserPagingEntity>,
    private val accessTokenManager: AccessTokenManager,
    private val query: String = ""
) : RemoteMediator<Int, UserPagingEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, UserPagingEntity>
    ): MediatorResult {
        try {
            val offset = when (loadType) {
                LoadType.REFRESH -> 0
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    state.lastItemOrNull()?.pagingId ?: return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                }
            }

            val loadSize = when (loadType) {
                LoadType.REFRESH -> state.config.initialLoadSize
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> state.config.pageSize
            }

            val accessToken = accessTokenManager.accessToken ?: return MediatorResult.Error(
                PaginationError.NoAccessToken
            )

            val users = if (query.isEmpty()) {
                val response = getSuggestions(accessToken.token, loadSize, offset)
                response.ifEmpty { searchUsers(accessToken.token, loadSize, offset) }
            } else {
                searchUsers(accessToken.token, loadSize, offset)
            }

            if (loadType == LoadType.REFRESH) {
                localRepository.deleteAllWithPrimaryKeys()
            }
            val entities = users.map { it.toUserEntity() }
            localRepository.upsertAll(*entities.toTypedArray())

            return MediatorResult.Success(endOfPaginationReached = users.isEmpty() && query.isNotEmpty())

        } catch (e: UnknownHostException) {
            logThrowable(e)
            return MediatorResult.Error(PaginationError.NoInternet)
        } catch (e: SocketTimeoutException) {
            logThrowable(e)
            return MediatorResult.Error(PaginationError.NoInternet)
        } catch (e: Exception) {
            logThrowable(e)
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getSuggestions(
        accessTokenValue: String,
        count: Int,
        offset: Int
    ): List<UserDto> {
        return remoteRepo.getSuggestions(
            accessToken = accessTokenValue,
            count = count,
            offset = offset,
            fields = FIELDS
        )
    }

    private suspend fun searchUsers(
        accessTokenValue: String,
        count: Int,
        offset: Int
    ): List<UserDto> {
        return remoteRepo.searchUsers(
            accessToken = accessTokenValue,
            count = count,
            offset = offset,
            query = query,
            fields = FIELDS
        )
    }

    class Factory @Inject constructor(
        private val remoteRepo: UsersRemoteRepository,
        private val localRepo: LocalRepository<UserPagingEntity>,
        private val accessTokenManager: AccessTokenManager
    ) : BaseFactory<String, UsersRemoteMediator> {
        override fun newInstance(initializationData: String): UsersRemoteMediator {
            return UsersRemoteMediator(
                remoteRepo = remoteRepo,
                localRepository = localRepo,
                accessTokenManager = accessTokenManager,
                query = initializationData
            )
        }
    }

    companion object {
        private val FIELDS = listOf("photo_200_orig")
    }

}


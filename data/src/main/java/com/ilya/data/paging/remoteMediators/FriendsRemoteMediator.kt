package com.ilya.data.paging.remoteMediators

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.ilya.core.appCommon.AccessTokenManager
import com.ilya.core.appCommon.BaseFactory
import com.ilya.core.util.logThrowable
import com.ilya.data.local.LocalRepository
import com.ilya.data.local.database.FriendEntity
import com.ilya.data.network.UsersRemoteRepository
import com.ilya.data.paging.PaginationError
import com.ilya.data.toFriendEntity
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class FriendsRemoteMediator private constructor(
    private val remoteRepository: UsersRemoteRepository,
    private val localRepository: LocalRepository<FriendEntity>,
    private val accessTokenManager: AccessTokenManager
) : RemoteMediator<Int, FriendEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, FriendEntity>
    ): MediatorResult {
        try {
            val offset = when (loadType) {
                LoadType.REFRESH -> 0
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> state.lastItemOrNull()?.databaseId ?: 0
            }

            val loadSize = when (loadType) {
                LoadType.REFRESH -> state.config.initialLoadSize
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> state.config.pageSize
            }

            val accessToken = accessTokenManager.accessToken ?: return MediatorResult.Error(
                PaginationError.NoAccessToken
            )

            val friends = remoteRepository.getFriends(
                accessToken = accessToken.token,
                count = loadSize,
                offset = offset,
                fields = FIELDS
            )

            if (loadType == LoadType.REFRESH) {
                localRepository.deleteAllWithPrimaryKeys()
            }
            val friendsEntities = friends.map { it.toFriendEntity() }
            localRepository.upsertAll(*friendsEntities.toTypedArray())

            return MediatorResult.Success(endOfPaginationReached = friends.isEmpty())

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

    class Factory @Inject constructor(
        private val localRepository: LocalRepository<FriendEntity>,
        private val remoteRepository: UsersRemoteRepository,
        private val accessTokenManager: AccessTokenManager
    ) : BaseFactory<Unit, FriendsRemoteMediator> {
        override fun newInstance(initializationData: Unit): FriendsRemoteMediator {
            return FriendsRemoteMediator(remoteRepository, localRepository, accessTokenManager)
        }

    }

    companion object {
        private val FIELDS = listOf("photo_200_orig")
    }

}


package com.ilya.data.network.repository

import com.ilya.data.network.FriendsManageRemoteRepository
import com.ilya.data.network.retrofit.api.FriendsManageVkApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class FriendsManageVkRepository @Inject constructor(
    private val friendsManageVkApi: FriendsManageVkApi
) : FriendsManageRemoteRepository {

    override suspend fun addFriend(accessToken: String, userId: Long) {
        withContext(Dispatchers.IO) { friendsManageVkApi.addFriend(accessToken, userId) }
    }

    override suspend fun deleteFriend(accessToken: String, userId: Long) {
        withContext(Dispatchers.IO) { friendsManageVkApi.deleteFriend(accessToken, userId) }
    }

}
package com.ilya.data.remote.repository

import com.ilya.data.remote.FriendsManageRemoteRepository
import com.ilya.data.remote.retrofit.api.FriendsManageVkApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Inject

internal class FriendsManageVkRepository @Inject constructor(
    retrofit: Retrofit
) : FriendsManageRemoteRepository {

    private val api = retrofit.create<FriendsManageVkApi>()

    override suspend fun addFriend(accessToken: String, userId: Long) {
        withContext(Dispatchers.IO) { api.addFriend(accessToken, userId) }
    }

    override suspend fun deleteFriend(accessToken: String, userId: Long) {
        withContext(Dispatchers.IO) { api.deleteFriend(accessToken, userId) }
    }

}
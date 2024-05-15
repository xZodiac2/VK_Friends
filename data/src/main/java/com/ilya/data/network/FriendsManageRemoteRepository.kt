package com.ilya.data.network

interface FriendsManageRemoteRepository {

    suspend fun addFriend(
        accessToken: String,
        userId: Long
    )

    suspend fun deleteFriend(
        accessToken: String,
        userId: Long
    )

}
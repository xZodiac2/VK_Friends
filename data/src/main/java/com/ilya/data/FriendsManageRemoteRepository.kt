package com.ilya.data

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
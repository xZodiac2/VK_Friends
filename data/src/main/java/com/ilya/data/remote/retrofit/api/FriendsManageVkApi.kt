package com.ilya.data.remote.retrofit.api

import com.ilya.data.remote.retrofit.CURRENT_API_VERSION
import retrofit2.http.POST
import retrofit2.http.Query

internal interface FriendsManageVkApi {

    @POST("friends.add?v=$CURRENT_API_VERSION")
    suspend fun addFriend(
        @Query("access_token") accessToken: String,
        @Query("user_id") userId: Long
    )

    @POST("friends.delete?v=$CURRENT_API_VERSION")
    suspend fun deleteFriend(
        @Query("access_token") accessToken: String,
        @Query("user_id") userId: Long
    )

}
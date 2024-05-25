package com.ilya.data.network.retrofit.api

import com.ilya.data.network.retrofit.CURRENT_API_VERSION
import retrofit2.http.GET
import retrofit2.http.Query


internal interface UserDataVkApi {

    @GET("users.get?v=$CURRENT_API_VERSION")
    suspend fun getUserData(
        @Query("access_token") accessToken: String,
        @Query("user_ids") userId: Long,
        @Query("fields") fields: String,
        @Query("name_case") nameCase: String
    ): UserDataResponse

    @GET("wall.get?v=$CURRENT_API_VERSION")
    suspend fun getWall(
        @Query("access_token") accessToken: String,
        @Query("owner_id") ownerId: Long,
        @Query("count") count: Int,
        @Query("offset") offset: Int
    ): WallResponse

    @GET("video.get?v=$CURRENT_API_VERSION")
    suspend fun getVideoData(
        @Query("access_token") accessToken: String,
        @Query("owner_id") ownerId: Long,
        @Query("videos") videoId: String,
    ): VideoExtendedDto

}


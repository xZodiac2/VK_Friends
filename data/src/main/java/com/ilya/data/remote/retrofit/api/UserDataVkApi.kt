package com.ilya.data.remote.retrofit.api

import com.ilya.data.remote.retrofit.CURRENT_API_VERSION
import com.ilya.data.remote.retrofit.api.dto.PhotosResponse
import com.ilya.data.remote.retrofit.api.dto.UserDataResponse
import com.ilya.data.remote.retrofit.api.dto.VideoExtendedResponse
import com.ilya.data.remote.retrofit.api.dto.WallResponse
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
    ): VideoExtendedResponse

    @GET("photos.getAll?v=$CURRENT_API_VERSION")
    suspend fun getPhotos(
        @Query("access_token") accessToken: String,
        @Query("owner_id") ownerId: Long,
        @Query("offset") offset: Int,
        @Query("count") count: Int,
        @Query("extended") extended: Int
    ) : PhotosResponse

}



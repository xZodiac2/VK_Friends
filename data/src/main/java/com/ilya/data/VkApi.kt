package com.ilya.data

import com.squareup.moshi.Json
import retrofit2.http.GET
import retrofit2.http.Query


internal interface VkApi {
    
    @GET("friends.get?v=${CURRENT_API_VERSION}")
    suspend fun getFriends(
        @Query("access_token") accessToken: String,
        @Query("fields") fields: List<String>,
    ): Response
    
    @GET("friends.search?v=${CURRENT_API_VERSION}")
    suspend fun searchFriends(
        @Query("user_id") userId: Long,
        @Query("fields") fields: List<String>
    ): Response
    
    companion object {
        const val BASE_URL = "https://api.vk.com/method/"
        private const val CURRENT_API_VERSION = 5.199
    }
    
}

data class Response(
    @Json(name = "response") val response: ResponseData,
)

data class ResponseData(
    @Json(name = "count") val count: Int,
    @Json(name = "items") val items: List<User>,
)

data class User(
    @Json(name = "id") val id: Long = 0,
    @Json(name = "first_name") val first_name: String = "",
    @Json(name = "last_name") val last_name: String = "",
    @Json(name = "photo_200_orig") val photo_200_orig: String = "",
)

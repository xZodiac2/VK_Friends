package com.ilya.data.network.retrofit

import com.squareup.moshi.Json
import retrofit2.http.GET
import retrofit2.http.Query


internal interface VkApi {

    @GET("friends.get?v=$CURRENT_API_VERSION")
    suspend fun getFriends(
        @Query("access_token") accessToken: String,
        @Query("fields") fields: List<String>,
        @Query("count") count: Int,
        @Query("offset") offset: Int,
    ): Response

    @GET("users.search?v=$CURRENT_API_VERSION")
    suspend fun searchUsers(
        @Query("access_token") accessToken: String,
        @Query("q") query: String,
        @Query("count") count: Int,
        @Query("offset") offset: Int,
        @Query("fields") fields: List<String>
    ): Response

    @GET("friends.getSuggestions?v=$CURRENT_API_VERSION")
    suspend fun getSuggestions(
        @Query("access_token") accessToken: String,
        @Query("count") count: Int,
        @Query("offset") offset: Int,
        @Query("fields") fields: List<String>
    ): Response

    companion object {
        const val BASE_URL = "https://api.vk.com/method/"
        private const val CURRENT_API_VERSION = 5.199
    }

}

data class Response(
    @Json(name = "response") val responseData: ResponseData,
)

data class ResponseData(
    @Json(name = "count") val count: Int,
    @Json(name = "items") val items: List<UserDto>,
)

data class UserDto(
    @Json(name = "id") val id: Long = 0,
    @Json(name = "first_name") val firstName: String = "",
    @Json(name = "last_name") val lastName: String = "",
    @Json(name = "photo_200_orig") val photoUrl: String = "",
)



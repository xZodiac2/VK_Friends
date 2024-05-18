package com.ilya.data.network.retrofit.api

import com.ilya.data.network.retrofit.CURRENT_API_VERSION
import com.squareup.moshi.Json
import retrofit2.http.GET
import retrofit2.http.Query


internal interface UsersVkApi {

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
    @Json(name = "sex") val sex: Int = 0,
    @Json(name = "photo_200_orig") val photoUrl: String = "",
    @Json(name = "bdate") val birthday: String = "",
    @Json(name = "status") val status: String = "",
    @Json(name = "city") val city: CityDto? = null,
    @Json(name = "relation") val relation: Int = 0,
    @Json(name = "relation_partner") val partner: PartnerDto? = null,
    @Json(name = "friend_status") val friendStatus: Int = 0,
    @Json(name = "counters") val counters: CountersDto? = null
)

data class PartnerDto(
    @Json(name = "id") val id: Long = 0,
    @Json(name = "first_name") val firstName: String = "",
    @Json(name = "last_name") val lastName: String = ""
)

data class CityDto(
    @Json(name = "title") val name: String = "",
    @Json(name = "id") val id: Int = 0
)

data class CountersDto(
    @Json(name = "friends") val friends: Int? = null,
    @Json(name = "followers") val followers: Int? = null,
    @Json(name = "subscriptions") val subscriptions: Int? = null
)
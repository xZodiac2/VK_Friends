package com.ilya.data.network.retrofit.api

import com.ilya.data.network.retrofit.CURRENT_API_VERSION
import com.squareup.moshi.Json
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

}

data class UserDataResponse(
    @Json(name = "response") val response: List<UserDto>
)


package com.ilya.data.retrofit.api

import com.ilya.data.retrofit.CURRENT_API_VERSION
import com.ilya.data.retrofit.api.dto.GetUsersResponse
import retrofit2.http.GET
import retrofit2.http.Query


internal interface UsersVkApi {

  @GET("friends.get?v=$CURRENT_API_VERSION")
  suspend fun getFriends(
    @Query("access_token") accessToken: String,
    @Query("fields") fields: List<String>,
    @Query("count") count: Int,
    @Query("offset") offset: Int,
  ): GetUsersResponse

  @GET("users.search?v=$CURRENT_API_VERSION")
  suspend fun searchUsers(
    @Query("access_token") accessToken: String,
    @Query("q") query: String,
    @Query("count") count: Int,
    @Query("offset") offset: Int,
    @Query("fields") fields: List<String>
  ): GetUsersResponse

  @GET("friends.getSuggestions?v=$CURRENT_API_VERSION")
  suspend fun getSuggestions(
    @Query("access_token") accessToken: String,
    @Query("count") count: Int,
    @Query("offset") offset: Int,
    @Query("fields") fields: List<String>
  ): GetUsersResponse

}


package com.ilya.data.retrofit.api

import com.ilya.data.retrofit.CURRENT_API_VERSION
import com.ilya.data.retrofit.api.dto.LikesResponse
import retrofit2.http.POST
import retrofit2.http.Query

internal interface LikesVkApi {

  @POST("likes.add?v=$CURRENT_API_VERSION")
  suspend fun addLike(
    @Query("access_token") accessToken: String,
    @Query("type") type: String,
    @Query("owner_id") ownerId: Long,
    @Query("item_id") itemId: Long,
  ): LikesResponse

  @POST("likes.delete?v=$CURRENT_API_VERSION")
  suspend fun deleteLike(
    @Query("access_token") accessToken: String,
    @Query("type") type: String,
    @Query("owner_id") ownerId: Long,
    @Query("item_id") itemId: Long,
  ): LikesResponse

}
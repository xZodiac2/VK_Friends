package com.ilya.data.retrofit.api

import com.ilya.data.retrofit.CURRENT_API_VERSION
import com.ilya.data.retrofit.api.dto.CommentsResponse
import com.ilya.data.retrofit.api.dto.WallResponse
import retrofit2.http.GET
import retrofit2.http.Query

internal interface WallVkApi {

  @GET("wall.get?v=$CURRENT_API_VERSION")
  suspend fun getWall(
    @Query("access_token") accessToken: String,
    @Query("owner_id") ownerId: Long,
    @Query("count") count: Int,
    @Query("offset") offset: Int
  ): WallResponse

  @GET("wall.getComments?v=$CURRENT_API_VERSION")
  suspend fun getComments(
    @Query("access_token") accessToken: String,
    @Query("owner_id") ownerId: Long,
    @Query("post_id") postId: Long,
    @Query("need_likes") needLikes: Int,
    @Query("offset") offset: Int,
    @Query("count") count: Int,
    @Query("extended") extended: Int,
    @Query("fields") fields: String,
    @Query("thread_items_count") threadCount: Int
  ): CommentsResponse

}
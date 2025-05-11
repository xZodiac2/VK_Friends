package com.ilya.data.retrofit.api

import com.ilya.data.retrofit.CURRENT_API_VERSION
import com.ilya.data.retrofit.api.dto.GroupResponse
import retrofit2.http.GET
import retrofit2.http.Query

internal interface GroupsVkApi {

  @GET("groups.getById?v=$CURRENT_API_VERSION")
  suspend fun getGroup(
    @Query("access_token") accessToken: String,
    @Query("group_id") groupId: Long
  ): GroupResponse

}
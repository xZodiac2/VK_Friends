package com.ilya.data.retrofit.api.dto

import com.squareup.moshi.Json


data class LikesResponse(
  @Json(name = "response") val response: LikesResponseData
)

data class LikesResponseData(
  @Json(name = "likes") val count: Int
)


data class LikesDto(
  @Json(name = "count") val count: Int = 0,
  @Json(name = "user_likes") val userLikes: Int = 0
)

package com.ilya.data.retrofit.api.dto

import com.squareup.moshi.Json


data class LikesResponse(
    @Json(name = "response") val response: LikesResponseData
)

data class LikesResponseData(
    @Json(name = "likes") val count: Int
)
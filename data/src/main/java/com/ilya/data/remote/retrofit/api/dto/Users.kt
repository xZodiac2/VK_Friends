package com.ilya.data.remote.retrofit.api.dto

import com.squareup.moshi.Json


data class UserDataResponse(
    @Json(name = "response") val response: List<UserDto>
)
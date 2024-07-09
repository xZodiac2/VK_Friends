package com.ilya.data.retrofit.api.dto

import com.squareup.moshi.Json

data class UserExtendedResponse(
    @Json(name = "response") val response: UserExtendedResponseData
)

data class UserExtendedResponseData(
    @Json(name = "user") val user: UserDto,
    @Json(name = "partner") val partner: PartnerDto?,
    @Json(name = "photos") val photos: List<PhotoDto>?
)
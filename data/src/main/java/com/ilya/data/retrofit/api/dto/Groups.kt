package com.ilya.data.retrofit.api.dto

import com.squareup.moshi.Json

data class GroupResponse(
    @Json(name = "response") val response: GroupResponseData
)

data class GroupResponseData(
    @Json(name = "groups") val groups: List<GroupDto>
)

data class GroupDto(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "photo_200") val photoUrl: String
)



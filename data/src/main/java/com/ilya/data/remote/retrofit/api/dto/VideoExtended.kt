package com.ilya.data.remote.retrofit.api.dto

import com.squareup.moshi.Json

data class VideoExtendedResponse(
    @Json(name = "response") val response: VideoExtendedDtoResponse
)

data class VideoExtendedDtoResponse(
    @Json(name = "count") val count: Int,
    @Json(name = "items") val items: List<VideoExtendedDataDto>
)

data class VideoExtendedDataDto(
    @Json(name = "duration") val duration: Int = 0,
    @Json(name = "first_frame") val firstFrame: List<FirstFrameDto>,
    @Json(name = "id") val id: Long = 0,
    @Json(name = "owner_id") val ownerId: Long = 0,
    @Json(name = "title") val title: String = "",
    @Json(name = "player") val playerUrl: String = ""
)

data class FirstFrameDto(
    @Json(name = "url") val url: String,
    @Json(name = "width") val width: Int,
    @Json(name = "height") val height: Int
)
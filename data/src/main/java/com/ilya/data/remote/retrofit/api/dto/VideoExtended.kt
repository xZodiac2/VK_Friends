package com.ilya.data.remote.retrofit.api.dto

import com.squareup.moshi.Json

data class VideoExtendedResponse(
    @Json(name = "response") val response: VideoExtendedResponseData
)

data class VideoExtendedResponseData(
    @Json(name = "count") val count: Int,
    @Json(name = "items") val items: List<VideoExtendedDto>
)

data class VideoExtendedDto(
    @Json(name = "duration") val duration: Int,
    @Json(name = "first_frame") val firstFrame: List<FirstFrameDto>,
    @Json(name = "id") val id: Long,
    @Json(name = "owner_id") val ownerId: Long,
    @Json(name = "title") val title: String,
    @Json(name = "player") val playerUrl: String,
    @Json(name = "likes") val likes: LikesDto
)

data class FirstFrameDto(
    @Json(name = "url") val url: String,
    @Json(name = "width") val width: Int,
    @Json(name = "height") val height: Int
)
package com.ilya.data.remote.retrofit.api.dto

import com.squareup.moshi.Json


data class AdditionalPostDataResponse(
    @Json(name = "response") val response: AdditionalPostData
)

data class AdditionalPostData(
    @Json(name = "videos") val videos: AdditionalVideosDataDto? = null,
    @Json(name = "post_owner") val postOwner: AdditionalPostOwnerDto,
    @Json(name = "photos") val photos: AdditionalPhotosDataDto? = null
)

data class AdditionalPhotosDataDto(
    @Json(name = "post_id") val postId: Long,
    @Json(name = "items") val items: List<PhotoDto>
)

data class AdditionalVideosDataDto(
    @Json(name = "post_id") val postId: Long,
    @Json(name = "items") val items: List<VideoAdditionalData>
)

data class VideoAdditionalData(
    @Json(name = "duration") val duration: Int = 0,
    @Json(name = "image") val firstFrame: List<FirstFrameDto>,
    @Json(name = "id") val id: Long = 0,
    @Json(name = "owner_id") val ownerId: Long = 0,
    @Json(name = "title") val title: String = "",
    @Json(name = "player") val playerUrl: String = "",
    @Json(name = "likes") val likes: LikesDto? = null
)

data class AdditionalPostOwnerDto(
    @Json(name = "post_id") val postId: Long,
    @Json(name = "data") val data: UserDto
)
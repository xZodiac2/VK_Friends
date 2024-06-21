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
    @Json(name = "items") val items: List<VideoExtendedDataDto>
)

data class AdditionalPostOwnerDto(
    @Json(name = "post_id") val postId: Long,
    @Json(name = "data") val data: UserDto
)
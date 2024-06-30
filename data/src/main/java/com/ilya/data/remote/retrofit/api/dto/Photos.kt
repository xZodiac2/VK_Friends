package com.ilya.data.remote.retrofit.api.dto

import com.squareup.moshi.Json

data class PhotosResponse(
    @Json(name = "response") val response: PhotosResponseData
)

data class PhotosResponseData(
    @Json(name = "items") val items: List<PhotoDto>,
    @Json(name = "count") val count: Int
)

data class PhotoDto(
    @Json(name = "album_id") val albumId: Int = 0,
    @Json(name = "id") override val id: Long = 0,
    @Json(name = "owner_id") override val ownerId: Long = 0,
    @Json(name = "sizes") val sizes: List<SizeDto>,
    @Json(name = "likes") val likes: LikesDto? = null,
    @Json(name = "access_key") override val accessKey: String = ""
) : BaseAttachment()
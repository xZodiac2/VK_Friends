package com.ilya.data.remote.retrofit.api.dto

import com.squareup.moshi.Json

data class WallResponse(
    @Json(name = "response") val response: WallResponseData
)

data class WallResponseData(
    @Json(name = "count") val count: Int,
    @Json(name = "items") val items: List<PostDto>
)

data class PostDto(
    @Json(name = "id") val id: Long,
    @Json(name = "text") val text: String = "",
    @Json(name = "attachments") val attachments: List<AttachmentDto>,
    @Json(name = "likes") val likes: LikesDto,
    @Json(name = "date") val dateUnixTime: Long,
    @Json(name = "from_id") val authorId: String
)

abstract class BaseAttachment {
    abstract val id: Long
    abstract val ownerId: Long
    open val accessKey: String = ""
}

data class AttachmentDto(
    @Json(name = "type") val type: String,
    @Json(name = "photo") val photo: PhotoDto? = null,
    @Json(name = "video") val video: VideoDto? = null,
    @Json(name = "audio") val audio: AudioDto? = null
)

data class AudioDto(
    @Json(name = "artist") val artist: String = "",
    @Json(name = "id") override val id: Long = 0,
    @Json(name = "owner_id") override val ownerId: Long = 0,
    @Json(name = "title") val title: String = "",
    @Json(name = "duration") val duration: Int = 0,
    @Json(name = "url") val url: String = "",
) : BaseAttachment()

data class VideoDto(
    @Json(name = "duration") val duration: Int = 0,
    @Json(name = "first_frame") val firstFrame: List<FirstFrameDto>? = null,
    @Json(name = "id") override val id: Long = 0,
    @Json(name = "owner_id") override val ownerId: Long = 0,
    @Json(name = "title") val title: String = "",
    @Json(name = "access_key") override val accessKey: String = ""
) : BaseAttachment()

data class LikesDto(
    @Json(name = "count") val count: Int = 0,
    @Json(name = "user_likes") val userLikes: Int = 0
)

data class SizeDto(
    @Json(name = "type") val type: String = "",
    @Json(name = "height") val height: Int = 0,
    @Json(name = "width") val width: Int = 0,
    @Json(name = "url") val url: String = ""
)

package com.ilya.data.retrofit.api.dto

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
  @Json(name = "text") val text: String,
  @Json(name = "attachments") val attachments: List<AttachmentDto>,
  @Json(name = "likes") val likes: LikesDto,
  @Json(name = "date") val dateUnixTime: Long,
  @Json(name = "from_id") val authorId: Long,
  @Json(name = "owner_id") val ownerId: Long,
  @Json(name = "copy_history") val cotyHistory: List<HistoryPostDto> = emptyList(),
  @Json(name = "comments") val commentsInfo: CommentsInfoDto
)

data class CommentsInfoDto(
  @Json(name = "can_view") val canView: Int,
  @Json(name = "can_post") val canPost: Int,
  @Json(name = "count") val count: Int
)

data class HistoryPostDto(
  @Json(name = "id") val id: Long,
  @Json(name = "text") val text: String,
  @Json(name = "attachments") val attachments: List<AttachmentDto> = emptyList(),
  @Json(name = "from_id") val authorId: Long,
)

data class AttachmentDto(
  @Json(name = "type") val type: String,
  @Json(name = "photo") val photo: PhotoDto? = null,
  @Json(name = "video") val video: VideoDto? = null,
  @Json(name = "audio") val audio: AudioDto? = null
)

data class AudioDto(
  @Json(name = "artist") val artist: String = "",
  @Json(name = "id") val id: Long = 0,
  @Json(name = "owner_id") val ownerId: Long = 0,
  @Json(name = "title") val title: String = "",
  @Json(name = "duration") val duration: Int = 0,
  @Json(name = "url") val url: String = "",
)


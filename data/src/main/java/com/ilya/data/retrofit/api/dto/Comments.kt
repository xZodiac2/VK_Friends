package com.ilya.data.retrofit.api.dto

import com.squareup.moshi.Json

data class CommentsResponse(
    @Json(name = "response") val response: CommentsResponseData
)

data class CommentsResponseData(
    @Json(name = "count") val count: Int,
    @Json(name = "profiles") val profiles: List<UserDto> = emptyList(),
    @Json(name = "post_author_id") val postAuthorId: Long,
    @Json(name = "items") val comments: List<CommentDto> = emptyList()
)


data class CommentDto(
    @Json(name = "date") val date: Long,
    @Json(name = "from_id") val fromId: Long,
    @Json(name = "post_id") val postId: Long = 0,
    @Json(name = "owner_id") val ownerId: Long = 0,
    @Json(name = "id") val id: Long,
    @Json(name = "text") val text: String,
    @Json(name = "thread") val thread: CommentsThreadDto,
    @Json(name = "likes") val likes: LikesDto? = null,
    @Json(name = "deleted") val deleted: Boolean = false
)

data class CommentsThreadDto(
    @Json(name = "count") val count: Int,
    @Json(name = "items") val comments: List<ThreadDto>,
)

data class ThreadDto(
    @Json(name = "date") val date: Long,
    @Json(name = "from_id") val fromId: Long = 0,
    @Json(name = "reply_to_comment") val replyToComment: Long,
    @Json(name = "post_id") val postId: Long = 0,
    @Json(name = "reply_to_user") val replyToUser: Long,
    @Json(name = "owner_id") val ownerId: Long = 0,
    @Json(name = "id") val id: Long,
    @Json(name = "text") val text: String,
    @Json(name = "likes") val likes: LikesDto? = null,
    @Json(name = "deleted") val deleted: Boolean = false
)
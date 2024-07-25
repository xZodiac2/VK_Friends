package com.ilya.paging.models


data class Comment(
    val date: String,
    val fromId: Long,
    val postId: Long,
    val ownerId: Long,
    val id: Long,
    val text: String,
    val thread: List<ThreadComment>,
    val likes: Likes,
    val owner: User?
)

data class ThreadComment(
    val date: String,
    val fromId: Long,
    val replyToComment: Long,
    val postId: Long,
    val replyToUser: User?,
    val owner: User?,
    val id: Long,
    val text: String,
    val likes: Likes
)


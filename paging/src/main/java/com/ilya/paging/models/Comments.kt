package com.ilya.paging.models

import com.ilya.core.appCommon.enums.ObjectType


data class Comment(
    val date: String,
    val postId: Long,
    val fromId: Long,
    override val ownerId: Long,
    override val id: Long,
    override val likes: Likes?,
    override val objectType: ObjectType = ObjectType.COMMENT,
    val owner: User?,
    val text: String,
    val isDeleted: Boolean,
    val thread: List<ThreadComment>
) : Likeable

data class ThreadComment(
    val date: String,
    val postId: Long,
    val fromId: Long,
    override val ownerId: Long,
    override val id: Long,
    override val likes: Likes?,
    override val objectType: ObjectType = ObjectType.COMMENT,
    val owner: User?,
    val text: String,
    val isDeleted: Boolean,
    val replyToUser: User?,
    val replyToComment: Long
) : Likeable


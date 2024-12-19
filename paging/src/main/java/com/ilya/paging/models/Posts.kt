package com.ilya.paging.models

import com.ilya.core.appCommon.enums.ObjectType


data class Post(
    val videos: List<Video>,
    val photos: List<Photo>,
    val audios: List<Audio>,
    val author: PostAuthor,
    val id: Long,
    val likes: Likes,
    val ownerId: Long,
    val commentsInfo: CommentsInfo,
    val date: String,
    val text: String,
    val reposted: RepostedPost?,
    val likeableCommonInfo: LikeableCommonInfo = LikeableCommonInfo(id, ownerId, likes, ObjectType.POST)
)

data class CommentsInfo(
    val count: Int,
    val canComment: Boolean,
    val canView: Boolean
)

data class RepostedPost(
    val videos: List<Video>,
    val photos: List<Photo>,
    val audios: List<Audio>,
    val owner: PostAuthor?,
    val group: Group?,
    val repostedByGroup: Boolean,
    val id: Long,
    val text: String,
)

data class Group(
    val id: Long,
    val name: String,
    val photoUrl: String
)

data class PostAuthor(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val photoUrl: String,
    val isPrivate: Boolean
)

data class Comment(
    val date: String,
    val postId: Long,
    val fromId: Long,
    val ownerId: Long,
    val id: Long,
    val likes: Likes?,
    val objectType: ObjectType = ObjectType.COMMENT,
    val owner: User?,
    val text: String,
    val isDeleted: Boolean,
    val thread: List<Comment>,
    val replyToUser: User?,
    val replyToComment: Long,
    val likeableCommonInfo: LikeableCommonInfo = LikeableCommonInfo(id, ownerId, likes, objectType)
)


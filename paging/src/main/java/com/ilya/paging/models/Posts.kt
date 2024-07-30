package com.ilya.paging.models

import com.ilya.core.appCommon.enums.ObjectType


data class Post(
    val videos: List<Video>,
    val photos: List<Photo>,
    val audios: List<Audio>,
    val author: PostAuthor,
    override val id: Long,
    override val likes: Likes,
    override val ownerId: Long,
    override val objectType: ObjectType = ObjectType.POST,
    val commentsInfo: CommentsInfo,
    val date: String,
    val text: String,
    val reposted: RepostedPost?
) : Likeable

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

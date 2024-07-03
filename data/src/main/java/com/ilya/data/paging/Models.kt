package com.ilya.data.paging

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val photoUrl: String,
    val isClosed: Boolean
) : Parcelable

data class Post(
    val videos: List<Video>,
    val photos: List<Photo>,
    val audios: List<Audio>,
    val author: User,
    val id: Long,
    val text: String,
    val likes: Likes,
    val authorId: Long = author.id,
    val ownerId: Long,
    val dateUnixTime: Long,
    val reposted: RepostedPost? = null,
)

data class RepostedPost(
    val videos: List<Video>,
    val photos: List<Photo>,
    val audios: List<Audio>,
    val owner: User?,
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

data class Audio(
    val artist: String,
    val id: Long,
    val ownerId: Long,
    val title: String,
    val duration: Int,
    val url: String,
)

data class Photo(
    val albumId: Int,
    val id: Long,
    val ownerId: Long,
    val likes: Likes?,
    val sizes: List<Size>,
    val accessKey: String
)

data class Video(
    val duration: Int = 0,
    val firstFrame: List<FirstFrame>,
    val id: Long,
    val ownerId: Long,
    val likes: Likes?,
    val title: String,
    val playerUrl: String
)

data class Likes(
    val count: Int,
    val userLikes: Boolean
)

data class Size(
    val type: String,
    val height: Int,
    val width: Int,
    val url: String
)

data class FirstFrame(
    val url: String,
    val width: Int,
    val height: Int
)

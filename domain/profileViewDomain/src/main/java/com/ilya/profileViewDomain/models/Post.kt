package com.ilya.profileViewDomain.models

import android.os.Parcelable
import com.ilya.core.appCommon.enums.PhotoSize
import kotlinx.parcelize.Parcelize

interface Likeable {
    val id: Long
    val ownerId: Long
    val likes: Likes?
}

abstract class Attachment

@Parcelize
data class Post(
    val videos: List<Video>,
    val photos: List<Photo>,
    val audios: List<Audio>,
    val author: PostAuthor,
    override val id: Long,
    override val likes: Likes,
    override val ownerId: Long = author.id,
    val date: String,
    val text: String,
    val reposted: RepostedPost?
) : Likeable, Parcelable

@Parcelize
data class RepostedPost(
    val videos: List<Video>,
    val photos: List<Photo>,
    val audios: List<Audio>,
    val owner: PostAuthor?,
    val group: Group?,
    val repostedByGroup: Boolean,
    val id: Long,
    val text: String,
) : Parcelable

@Parcelize
data class Group(
    val id: Long,
    val name: String,
    val photoUrl: String
) : Parcelable

@Parcelize
data class PostAuthor(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val photoUrl: String
) : Parcelable

@Parcelize
data class Audio(
    val artist: String,
    val id: Long,
    val ownerId: Long,
    val title: String,
    val duration: Int,
    val url: String,
) : Attachment(), Parcelable

@Parcelize
data class Photo(
    val albumId: Int,
    override val id: Long,
    override val ownerId: Long,
    override val likes: Likes?,
    val sizes: List<Size>,
    val accessKey: String
) : Attachment(), Likeable, Parcelable

@Parcelize
data class Video(
    val duration: Int = 0,
    val firstFrame: List<FirstFrame>,
    val id: Long,
    val ownerId: Long,
    val title: String,
    val accessKey: String
) : Attachment(), Parcelable

data class VideoExtended(
    val duration: Int = 0,
    val firstFrame: List<FirstFrame>,
    override val id: Long,
    override val ownerId: Long,
    override val likes: Likes?,
    val title: String,
    val playerUrl: String
) : Likeable

@Parcelize
data class Likes(
    val count: Int,
    val userLikes: Boolean
) : Parcelable

@Parcelize
data class Size(
    val type: PhotoSize,
    val height: Int,
    val width: Int,
    val url: String
) : Parcelable

@Parcelize
data class FirstFrame(
    val url: String,
    val width: Int,
    val height: Int
) : Parcelable


fun Likes.toggled(): Likes {
    return this.copy(
        userLikes = !this.userLikes,
        count = if (this.userLikes) this.count - 1 else this.count + 1
    )
}

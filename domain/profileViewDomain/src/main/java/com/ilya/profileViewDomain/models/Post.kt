package com.ilya.profileViewDomain.models

import android.os.Parcelable
import com.ilya.core.appCommon.enums.PhotoSize
import kotlinx.parcelize.Parcelize

interface Likeable {
    val id: Long
    val ownerId: Long
    val likes: Likes?
}

interface Attachment

@Parcelize
data class Post(
    val videos: List<VideoExtended>,
    val photos: List<Photo>,
    val audios: List<Audio>,
    val owner: PostOwner,
    override val id: Long,
    override val likes: Likes,
    override val ownerId: Long = owner.id,
    val date: String,
    val text: String,
    val reposted: RepostedPost?
) : Likeable, Parcelable

@Parcelize
data class RepostedPost(
    val videos: List<VideoExtended>,
    val photos: List<Photo>,
    val audios: List<Audio>,
    val owner: PostOwner?,
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
data class PostOwner(
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
) : Attachment, Parcelable

@Parcelize
data class Photo(
    val albumId: Int,
    override val id: Long,
    override val ownerId: Long,
    override val likes: Likes?,
    val sizes: List<Size>,
    val accessKey: String
) : Attachment, Likeable, Parcelable

@Parcelize
data class VideoExtended(
    val duration: Int = 0,
    val firstFrame: List<FirstFrame>,
    override val id: Long,
    override val ownerId: Long,
    override val likes: Likes?,
    val title: String,
    val playerUrl: String
) : Attachment, Likeable, Parcelable

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

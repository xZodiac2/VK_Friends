package com.ilya.paging

import com.ilya.core.appCommon.enums.PhotoSize

data class User(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val photoUrl: String,
    val isClosed: Boolean
)

interface Likeable {
    val id: Long
    val ownerId: Long
    val likes: Likes?
}

abstract class Attachment

data class Post(
    val videos: List<Video>,
    val photos: List<Photo>,
    val audios: List<Audio>,
    val author: PostAuthor,
    override val id: Long,
    override val likes: Likes,
    override val ownerId: Long,
    val date: String,
    val text: String,
    val reposted: RepostedPost?
) : Likeable

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

data class Audio(
    val artist: String,
    val id: Long,
    val ownerId: Long,
    val title: String,
    val duration: Int,
    val url: String,
) : Attachment()

data class Photo(
    val albumId: Int,
    override val id: Long,
    override val ownerId: Long,
    override val likes: Likes?,
    val sizes: List<Size>,
    val accessKey: String
) : Attachment(), Likeable

data class Video(
    val duration: Int = 0,
    val firstFrame: List<FirstFrame>,
    val id: Long,
    val ownerId: Long,
    val title: String,
    val accessKey: String
) : Attachment()

data class VideoExtended(
    val duration: Int = 0,
    val firstFrame: List<FirstFrame>,
    override val id: Long,
    override val ownerId: Long,
    override val likes: Likes?,
    val title: String,
    val playerUrl: String
) : Likeable

data class Likes(
    val count: Int,
    val userLikes: Boolean
)

data class Size(
    val type: PhotoSize,
    val height: Int,
    val width: Int,
    val url: String
)

data class FirstFrame(
    val url: String,
    val width: Int,
    val height: Int
)


fun Likes.toggled(): Likes {
    return this.copy(
        userLikes = !this.userLikes,
        count = if (this.userLikes) this.count - 1 else this.count + 1
    )
}

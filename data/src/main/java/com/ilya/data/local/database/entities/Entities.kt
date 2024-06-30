package com.ilya.data.local.database.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

// Common

data class UserData(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val photoUrl: String
)

// Paging

@Entity(tableName = "friends_paging")
data class FriendPagingEntity(
    @PrimaryKey(autoGenerate = true)
    val pagingId: Int = 0,
    @Embedded
    val data: UserData
)

@Entity(tableName = "users_paging")
data class UserPagingEntity(
    @PrimaryKey(autoGenerate = true)
    val pagingId: Int = 0,
    @Embedded
    val data: UserData
)

@Entity(tableName = "posts")
data class PostPagingEntity(
    @PrimaryKey(autoGenerate = true)
    val pagingId: Int = 0,
    val id: Long,
    val text: String,
    val dateUnixTime: Long
)

// Posts

@Entity(tableName = "post_owners")
data class PostOwnerEntity(
    @PrimaryKey
    val postId: Long,
    @Embedded val data: UserData
)

@Entity(tableName = "photos")
data class PhotoEntity(
    @PrimaryKey
    val postId: Long,
    val albumId: Int,
    val id: Long,
    val ownerId: Long,
)

@Entity(tableName = "photo_sizes")
data class SizeEntity(
    @PrimaryKey
    val photoId: Long,
    val type: String,
    val height: Int,
    val width: Int,
    val url: String
)

@Entity(tableName = "first_frames")
data class FirstFrameEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val videoId: Long,
    val url: String,
    val width: Int,
    val height: Int,
)

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey
    val id: Long,
    val postId: Long,
    val duration: Int = 0,
    val ownerId: Long,
    val title: String,
    val playerUrl: String
)

@Entity(tableName = "audios")
data class AudioEntity(
    @PrimaryKey
    val id: Long,
    val postId: Long,
    val artist: String,
    val ownerId: Long,
    val title: String,
    val duration: Int,
    val url: String,
)

@Entity(tableName = "post_likes")
data class PostLikesEntity(
    @PrimaryKey
    val postId: Long,
    val count: Int,
    val userLikes: Boolean
)

@Entity(tableName = "video_likes")
data class VideoLikesEntity(
    @PrimaryKey
    val videoId: Long,
    val count: Int,
    val userLikes: Boolean
)

@Entity(tableName = "photo_likes")
data class PhotoLikesEntity(
    @PrimaryKey
    val photoId: Long,
    val count: Int,
    val userLikes: Boolean
)

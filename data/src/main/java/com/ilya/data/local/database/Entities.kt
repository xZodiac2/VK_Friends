package com.ilya.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "friends_table")
data class FriendEntity(
    @PrimaryKey(autoGenerate = true)
    val databaseId: Int = 0,
    val id: Long,
    val firstName: String,
    val lastName: String,
    val photoUrl: String
)

@Entity(tableName = "users_table")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val databaseId: Int = 0,
    val id: Long,
    val firstName: String,
    val lastName: String,
    val photoUrl: String
)


@Entity(tableName = "wall_items_table")
data class WallItemEntity(
    @PrimaryKey(autoGenerate = true)
    val databaseId: Int = 0,
    val attachments: Attachments,
    val likes: Likes
)

data class Attachments(
    val items: List<Attachment>
)

data class Attachment(
    val type: String,
    val photo: Photo,
    val video: Video,
    val audio: Audio
)

data class Audio(
    val artist: String,
    val id: Long,
    val ownerId: Long,
    val title: String,
    val duration: Int,
    val url: String,
    val dateUnixTime: Long
)

data class Photo(
    val albumId: Int,
    val dateUnixTime: Long,
    val id: Long,
    val ownerId: Long,
    val sizes: List<Size>
)

data class Video(
    val dateUnixTime: Long,
    val duration: Int,
    val firstFrame: List<Photo>,
    val id: Long,
    val ownerId: Long,
    val title: String,
    val accessKey: String
)

data class Likes(
    val count: Int,
    val userLikes: Int
)

data class Size(
    val type: Char,
    val height: Int,
    val width: Int,
    val url: String
)
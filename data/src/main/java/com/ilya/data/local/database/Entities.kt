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
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val databaseId: Int = 0,
    val id: Long,
    val owner: PostOwnerDatabaseDto,
    val attachments: AttachmentsDatabaseDto,
    val likes: LikesDatabaseDto,
    val dateUnixTime: Long
)


data class PostOwnerDatabaseDto(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val photoUrl: String,
)


data class AttachmentsDatabaseDto(
    val items: List<AttachmentDatabaseDto>
)

data class AttachmentDatabaseDto(
    val type: String,
    val photo: PhotoDatabaseDto? = null,
    val video: VideoExtendedDatabaseDto? = null,
    val audio: AudioDatabaseDto? = null
)

data class AudioDatabaseDto(
    val artist: String,
    val id: Long,
    val ownerId: Long,
    val title: String,
    val duration: Int,
    val url: String,
)

data class PhotoDatabaseDto(
    val albumId: Int,
    val id: Long,
    val ownerId: Long,
    val sizes: List<SizeDatabaseDto>?
)

data class VideoExtendedDatabaseDto(
    val duration: Int = 0,
    val firstFrame: List<PhotoDatabaseDto>?,
    val id: Long,
    val ownerId: Long,
    val title: String,
    val playerUrl: String
)

data class LikesDatabaseDto(
    val count: Int,
    val userLikes: Boolean
)

data class SizeDatabaseDto(
    val type: String,
    val height: Int,
    val width: Int,
    val url: String
)
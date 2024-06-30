package com.ilya.data.local.database.entities

import androidx.room.Embedded
import androidx.room.Relation

data class PhotoWithSizesAndLikes(
    @Embedded
    val photo: PhotoEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "photoId"
    )
    val sizes: List<SizeEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "photoId"
    )
    val likes: PhotoLikesEntity?
)

data class VideoWithFirstFramesAndLikes(
    @Embedded
    val video: VideoEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "videoId"
    )
    val firstFrames: List<FirstFrameEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "videoId"
    )
    val likes: VideoLikesEntity?
)

data class PostWithAttachmentsAndOwner(
    @Embedded
    val data: PostPagingEntity,
    @Relation(
        entity = PhotoEntity::class,
        parentColumn = "id",
        entityColumn = "postId"
    )
    val photos: List<PhotoWithSizesAndLikes>,
    @Relation(
        entity = VideoEntity::class,
        parentColumn = "id",
        entityColumn = "postId"
    )
    val videos: List<VideoWithFirstFramesAndLikes>,
    @Relation(
        parentColumn = "id",
        entityColumn = "postId"
    )
    val audios: List<AudioEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "postId"
    )
    val owner: PostOwnerEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "postId"
    )
    val likes: PostLikesEntity
)

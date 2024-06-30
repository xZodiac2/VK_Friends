package com.ilya.data.mappers

import com.ilya.data.local.database.entities.PhotoLikesEntity
import com.ilya.data.local.database.entities.PostLikesEntity
import com.ilya.data.local.database.entities.VideoLikesEntity
import com.ilya.data.remote.retrofit.api.dto.LikesDto


fun LikesDto.toPostLikesEntity(postId: Long): PostLikesEntity {
    return PostLikesEntity(
        postId = postId,
        count = count,
        userLikes = userLikes == 1
    )
}

fun LikesDto.toPhotoLikesEntity(photoId: Long): PhotoLikesEntity {
    return PhotoLikesEntity(
        photoId = photoId,
        count = count,
        userLikes = userLikes == 1
    )
}

fun LikesDto.toVideoLikesEntity(videoId: Long): VideoLikesEntity {
    return VideoLikesEntity(
        videoId = videoId,
        count = count,
        userLikes = userLikes == 1
    )
}

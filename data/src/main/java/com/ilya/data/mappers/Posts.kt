package com.ilya.data.mappers

import com.ilya.data.local.database.entities.AudioEntity
import com.ilya.data.local.database.entities.FirstFrameEntity
import com.ilya.data.local.database.entities.PhotoEntity
import com.ilya.data.local.database.entities.PhotoWithSizesAndLikes
import com.ilya.data.local.database.entities.PostOwnerEntity
import com.ilya.data.local.database.entities.PostPagingEntity
import com.ilya.data.local.database.entities.PostWithAttachmentsAndOwner
import com.ilya.data.local.database.entities.SizeEntity
import com.ilya.data.local.database.entities.UserData
import com.ilya.data.local.database.entities.VideoEntity
import com.ilya.data.local.database.entities.VideoWithFirstFramesAndLikes
import com.ilya.data.remote.retrofit.api.dto.AudioDto
import com.ilya.data.remote.retrofit.api.dto.FirstFrameDto
import com.ilya.data.remote.retrofit.api.dto.PhotoDto
import com.ilya.data.remote.retrofit.api.dto.PostDto
import com.ilya.data.remote.retrofit.api.dto.UserDto
import com.ilya.data.remote.retrofit.api.dto.VideoAdditionalData
import com.ilya.data.remote.retrofit.api.dto.VideoExtendedDataDto

private const val AUDIO_TYPE = "audio"

fun PostDto.toPostEntity(
    videos: List<VideoAdditionalData>,
    photos: List<PhotoDto>,
    owner: UserDto
): PostWithAttachmentsAndOwner {

    return PostWithAttachmentsAndOwner(
        data = PostPagingEntity(
            id = id,
            text = text,
            dateUnixTime = dateUnixTime
        ),
        photos = photos.map {
            val photoWithSizes = it.toPhotoWithSizes(id)
            PhotoWithSizesAndLikes(
                photoWithSizes.first,
                photoWithSizes.second,
                it.likes?.toPhotoLikesEntity(it.id)
            )
        },
        videos = videos.map {
            val videoWithFirstFrames = it.toVideoWithFirstFrames(id)
            VideoWithFirstFramesAndLikes(
                videoWithFirstFrames.first,
                videoWithFirstFrames.second,
                it.likes?.toVideoLikesEntity(it.id)
            )
        },
        audios = attachments.mapNotNull { attachment ->
            if (attachment.type == AUDIO_TYPE) {
                attachment.audio?.toAudioEntity(id)
            } else {
                null
            }
        },
        owner = owner.toPostOwnerEntity(id),
        likes = likes.toPostLikesEntity(id)
    )
}



private fun AudioDto.toAudioEntity(postId: Long): AudioEntity {
    return AudioEntity(
        id = id,
        artist = artist,
        postId = postId,
        ownerId = ownerId,
        title = title,
        url = url,
        duration = duration
    )
}




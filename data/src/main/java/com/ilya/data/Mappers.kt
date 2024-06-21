package com.ilya.data

import com.ilya.data.local.database.entities.AudioEntity
import com.ilya.data.local.database.entities.FirstFrameEntity
import com.ilya.data.local.database.entities.FriendPagingEntity
import com.ilya.data.local.database.entities.PhotoEntity
import com.ilya.data.local.database.entities.PhotoWithSizes
import com.ilya.data.local.database.entities.PostLikesEntity
import com.ilya.data.local.database.entities.PostOwnerEntity
import com.ilya.data.local.database.entities.PostPagingEntity
import com.ilya.data.local.database.entities.PostWithAttachmentsAndOwner
import com.ilya.data.local.database.entities.SizeEntity
import com.ilya.data.local.database.entities.UserData
import com.ilya.data.local.database.entities.UserPagingEntity
import com.ilya.data.local.database.entities.VideoEntity
import com.ilya.data.local.database.entities.VideoWithFirstFrames
import com.ilya.data.paging.User
import com.ilya.data.remote.retrofit.api.dto.AudioDto
import com.ilya.data.remote.retrofit.api.dto.FirstFrameDto
import com.ilya.data.remote.retrofit.api.dto.LikesDto
import com.ilya.data.remote.retrofit.api.dto.PhotoDto
import com.ilya.data.remote.retrofit.api.dto.PostDto
import com.ilya.data.remote.retrofit.api.dto.SizeDto
import com.ilya.data.remote.retrofit.api.dto.UserDto
import com.ilya.data.remote.retrofit.api.dto.VideoExtendedDataDto
import com.vk.id.AccessToken
import com.vk.id.VKIDUser

fun VKIDUser.toUser(accessToken: AccessToken): User {
    return User(
        id = accessToken.userID,
        firstName = firstName,
        lastName = lastName,
        photoUrl = photo200 ?: ""
    )
}

fun UserDto.toFriendEntity(): FriendPagingEntity {
    return FriendPagingEntity(
        data = UserData(
            id = id,
            firstName = firstName,
            lastName = lastName,
            photoUrl = photoUrl
        )
    )
}

fun UserDto.toUserEntity(): UserPagingEntity {
    return UserPagingEntity(
        data = UserData(
            id = id,
            firstName = firstName,
            lastName = lastName,
            photoUrl = photoUrl
        )
    )
}

fun UserPagingEntity.toUser(): User = with(data) {
    return User(
        id = id,
        firstName = firstName,
        lastName = lastName,
        photoUrl = photoUrl
    )
}

fun FriendPagingEntity.toUser(): User = with(data) {
    return User(
        id = id,
        firstName = firstName,
        lastName = lastName,
        photoUrl = photoUrl
    )
}

private const val AUDIO_TYPE = "audio"

fun PostDto.toPostEntity(
    videos: List<VideoExtendedDataDto>,
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
            PhotoWithSizes(photoWithSizes.first, photoWithSizes.second)
        },
        videos = videos.map {
            val videoWithFirstFrames = it.toVideoWithFirstFrames(id)
            VideoWithFirstFrames(videoWithFirstFrames.first, videoWithFirstFrames.second)
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

private fun VideoExtendedDataDto.toVideoWithFirstFrames(postId: Long): Pair<VideoEntity, List<FirstFrameEntity>> {
    return this.toVideoEntity(postId) to firstFrame.map { it.toFirstFrameEntity(this.id) }
}

private fun PhotoDto.toPhotoWithSizes(postId: Long): Pair<PhotoEntity, List<SizeEntity>> {
    return this.toPhotoEntity(postId) to sizes.map { it.toSizeEntity(id) }
}

private fun FirstFrameDto.toFirstFrameEntity(videoId: Long): FirstFrameEntity {
    return FirstFrameEntity(
        videoId = videoId,
        url = url,
        width = width,
        height = height
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

private fun UserDto.toPostOwnerEntity(postId: Long): PostOwnerEntity {
    return PostOwnerEntity(
        postId = postId,
        data = UserData(
            id = id,
            firstName = firstName,
            lastName = lastName,
            photoUrl = photoUrl
        )
    )
}

private fun VideoExtendedDataDto.toVideoEntity(postId: Long): VideoEntity {
    return VideoEntity(
        postId = postId,
        duration = duration,
        id = id,
        ownerId = ownerId,
        title = title,
        playerUrl = playerUrl
    )
}

private fun PhotoDto.toPhotoEntity(postId: Long): PhotoEntity {
    return PhotoEntity(
        albumId = albumId,
        id = id,
        ownerId = ownerId,
        postId = postId
    )
}

private fun SizeDto.toSizeEntity(photoId: Long): SizeEntity {
    return SizeEntity(
        type = type,
        height = height,
        width = width,
        url = url,
        photoId = photoId
    )
}

private fun LikesDto.toPostLikesEntity(postId: Long): PostLikesEntity {
    return PostLikesEntity(
        postId = postId,
        count = count,
        userLikes = userLikes == 1
    )
}

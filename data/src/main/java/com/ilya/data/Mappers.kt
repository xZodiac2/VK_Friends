package com.ilya.data

import com.ilya.data.local.database.AttachmentDatabaseDto
import com.ilya.data.local.database.AttachmentsDatabaseDto
import com.ilya.data.local.database.AudioDatabaseDto
import com.ilya.data.local.database.FriendEntity
import com.ilya.data.local.database.LikesDatabaseDto
import com.ilya.data.local.database.PhotoDatabaseDto
import com.ilya.data.local.database.PostEntity
import com.ilya.data.local.database.PostOwnerDatabaseDto
import com.ilya.data.local.database.SizeDatabaseDto
import com.ilya.data.local.database.UserEntity
import com.ilya.data.local.database.VideoExtendedDatabaseDto
import com.ilya.data.network.retrofit.api.AttachmentDto
import com.ilya.data.network.retrofit.api.AudioDto
import com.ilya.data.network.retrofit.api.LikesDto
import com.ilya.data.network.retrofit.api.PhotoDto
import com.ilya.data.network.retrofit.api.PostDto
import com.ilya.data.network.retrofit.api.SizeDto
import com.ilya.data.network.retrofit.api.UserDto
import com.ilya.data.network.retrofit.api.VideoExtendedDataDto
import com.ilya.data.paging.User
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

fun UserDto.toFriendEntity(): FriendEntity {
    return FriendEntity(
        id = id,
        firstName = firstName,
        lastName = lastName,
        photoUrl = photoUrl
    )
}

fun UserDto.toUserEntity(): UserEntity {
    return UserEntity(
        id = id,
        firstName = firstName,
        lastName = lastName,
        photoUrl = photoUrl
    )
}

fun UserEntity.toUser(): User {
    return User(
        id = id,
        firstName = firstName,
        lastName = lastName,
        photoUrl = photoUrl
    )
}

fun FriendEntity.toUser(): User {
    return User(
        id = id,
        firstName = firstName,
        lastName = lastName,
        photoUrl = photoUrl
    )
}

fun PostDto.toPostEntity(videos: List<VideoExtendedDataDto>, owner: UserDto): PostEntity {
    var videoIndex = 0

    return PostEntity(
        id = id,
        attachments = AttachmentsDatabaseDto(attachments.map {
            if (it.type == "video") {
                val video = videos[videoIndex]
                videoIndex++
                it.toAttachmentDatabaseDto(video)
            } else {
                it.toAttachmentDatabaseDto(null)
            }
        }),
        likes = likes.toLikesDatabaseDto(),
        dateUnixTime = dateUnixTime,
        owner = owner.toPostOwnerDatabaseDto(),
        text = text
    )
}

private fun UserDto.toPostOwnerDatabaseDto(): PostOwnerDatabaseDto {
    return PostOwnerDatabaseDto(
        id = id,
        firstName = firstName,
        lastName = lastName,
        photoUrl = photoUrl
    )
}

private fun AttachmentDto.toAttachmentDatabaseDto(
    videoExtendedDto: VideoExtendedDataDto?
): AttachmentDatabaseDto {
    return AttachmentDatabaseDto(
        type = type,
        photo = photo?.toPhotoDatabaseDto(),
        audio = audio?.toAudioDatabaseDto(),
        video = videoExtendedDto?.toVideoExtendedDatabaseDto()
    )
}

private fun VideoExtendedDataDto.toVideoExtendedDatabaseDto(): VideoExtendedDatabaseDto {
    return VideoExtendedDatabaseDto(
        duration = duration,
        firstFrame = firstFrame?.map { it.toPhotoDatabaseDto() },
        id = id,
        ownerId = ownerId,
        title = title,
        playerUrl = playerUrl
    )
}

private fun AudioDto.toAudioDatabaseDto(): AudioDatabaseDto {
    return AudioDatabaseDto(
        artist = artist,
        id = id,
        ownerId = ownerId,
        title = title,
        duration = duration,
        url = url,
    )
}

private fun PhotoDto.toPhotoDatabaseDto(): PhotoDatabaseDto {
    return PhotoDatabaseDto(
        albumId = albumId,
        id = id,
        ownerId = ownerId,
        sizes = sizes?.map { it.toSizeDatabaseDto() }
    )
}

private fun SizeDto.toSizeDatabaseDto(): SizeDatabaseDto {
    return SizeDatabaseDto(
        type = type,
        height = height,
        width = width,
        url = url
    )
}

private fun LikesDto.toLikesDatabaseDto(): LikesDatabaseDto {
    return LikesDatabaseDto(
        count = count,
        userLikes = userLikes == 1
    )
}
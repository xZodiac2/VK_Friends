package com.ilya.profileViewDomain.mappers

import com.ilya.core.appCommon.enums.PhotoSize
import com.ilya.data.local.database.entities.AudioEntity
import com.ilya.data.local.database.entities.FirstFrameEntity
import com.ilya.data.local.database.entities.PhotoLikesEntity
import com.ilya.data.local.database.entities.PhotoWithSizesAndLikes
import com.ilya.data.local.database.entities.PostLikesEntity
import com.ilya.data.local.database.entities.PostOwnerEntity
import com.ilya.data.local.database.entities.PostWithAttachmentsAndOwner
import com.ilya.data.local.database.entities.SizeEntity
import com.ilya.data.local.database.entities.VideoLikesEntity
import com.ilya.data.local.database.entities.VideoWithFirstFramesAndLikes
import com.ilya.data.remote.retrofit.api.dto.LikesDto
import com.ilya.data.remote.retrofit.api.dto.PhotoDto
import com.ilya.data.remote.retrofit.api.dto.SizeDto
import com.ilya.profileViewDomain.models.Audio
import com.ilya.profileViewDomain.models.FirstFrame
import com.ilya.profileViewDomain.models.Likes
import com.ilya.profileViewDomain.models.Photo
import com.ilya.profileViewDomain.models.Post
import com.ilya.profileViewDomain.models.PostOwner
import com.ilya.profileViewDomain.models.Size
import com.ilya.profileViewDomain.models.VideoExtended
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun PostWithAttachmentsAndOwner.toPost(): Post {
    return Post(
        id = data.id,
        videos = videos.map { it.toVideoExtended() },
        audios = audios.map { it.toAudio() },
        photos = photos.map { it.toPhoto() },
        date = parseToString(data.dateUnixTime),
        likes = likes.toLikes(),
        owner = owner.toPostOwner()
    )
}

private fun PostOwnerEntity.toPostOwner(): PostOwner = with(data) {
    return PostOwner(
        id = id,
        firstName = firstName,
        lastName = lastName,
        photoUrl = photoUrl
    )
}

private fun AudioEntity.toAudio(): Audio {
    return Audio(
        artist = artist,
        id = id,
        ownerId = ownerId,
        title = title,
        duration = duration,
        url = url,
    )
}

private fun VideoWithFirstFramesAndLikes.toVideoExtended(): VideoExtended {
    return VideoExtended(
        duration = video.duration,
        firstFrame = firstFrames.map { it.toFirstFrame() },
        id = video.id,
        ownerId = video.ownerId,
        title = video.title,
        playerUrl = video.playerUrl,
        likes = likes?.toLikes()
    )
}

private fun FirstFrameEntity.toFirstFrame(): FirstFrame {
    return FirstFrame(
        url = url,
        width = width,
        height = height
    )
}

private fun VideoLikesEntity.toLikes(): Likes {
    return Likes(
        count = count,
        userLikes = userLikes
    )
}

fun PhotoDto.toPhoto(): Photo {
    return Photo(
        albumId = albumId,
        id = id,
        ownerId = ownerId,
        sizes = sizes.map { it.toSize() },
        likes = likes?.toLikes()
    )
}

private fun LikesDto.toLikes(): Likes {
    return Likes(
        count = count,
        userLikes = userLikes == 1
    )
}

private fun PhotoWithSizesAndLikes.toPhoto(): Photo {
    return Photo(
        ownerId = photo.ownerId,
        albumId = photo.albumId,
        id = photo.id,
        sizes = sizes.map { it.toSize() },
        likes = likes?.toLikes()
    )
}

private fun PostLikesEntity.toLikes(): Likes {
    return Likes(
        count = count,
        userLikes = userLikes
    )
}

private fun PhotoLikesEntity.toLikes(): Likes {
    return Likes(
        count = count,
        userLikes = userLikes
    )
}

private fun SizeDto.toSize(): Size {
    return Size(
        type = PhotoSize.entries.find { it.value == type } ?: PhotoSize.NOT_STATED,
        height = height,
        width = width,
        url = url
    )
}

private fun SizeEntity.toSize(): Size {
    return Size(
        type = PhotoSize.entries.find { it.value == type } ?: PhotoSize.NOT_STATED,
        height = height,
        width = width,
        url = url
    )
}

private fun parseToString(dateUnixTime: Long): String {
    val date = Date(dateUnixTime * 1000L)
    val formatter = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
    return formatter.format(date)
}
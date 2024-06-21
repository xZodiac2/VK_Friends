package com.ilya.profileViewDomain

import com.ilya.core.appCommon.enums.FriendStatus
import com.ilya.core.appCommon.enums.PhotoSize
import com.ilya.core.appCommon.enums.Relation
import com.ilya.core.appCommon.enums.Sex
import com.ilya.data.local.database.entities.AudioEntity
import com.ilya.data.local.database.entities.FirstFrameEntity
import com.ilya.data.local.database.entities.PhotoWithSizes
import com.ilya.data.local.database.entities.PostLikesEntity
import com.ilya.data.local.database.entities.PostOwnerEntity
import com.ilya.data.local.database.entities.PostWithAttachmentsAndOwner
import com.ilya.data.local.database.entities.SizeEntity
import com.ilya.data.local.database.entities.VideoWithFirstFrames
import com.ilya.data.remote.retrofit.api.dto.CityDto
import com.ilya.data.remote.retrofit.api.dto.CountersDto
import com.ilya.data.remote.retrofit.api.dto.PartnerDto
import com.ilya.data.remote.retrofit.api.dto.PhotoDto
import com.ilya.data.remote.retrofit.api.dto.SizeDto
import com.ilya.data.remote.retrofit.api.dto.UserDto
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun UserDto.toUser(photos: List<PhotoDto>): User {
    return User(
        id = id,
        firstName = firstName,
        lastName = lastName,
        photoUrl = photoUrl,
        friendStatus = FriendStatus.entries.find { it.value == friendStatus }
            ?: FriendStatus.NOT_FRIENDS,
        birthday = birthday,
        status = status,
        city = city?.toCity(),
        relation = Relation.entries.find { it.value == relation } ?: Relation.NOT_STATED,
        partner = partner?.toPartner(),
        sex = Sex.entries.find { it.value == sex } ?: Sex.NOT_STATED,
        counters = counters?.toCounters(),
        photos = photos.map { it.toPhoto() }
    )
}

private fun CountersDto.toCounters(): Counters {
    return Counters(
        friends = friends,
        subscriptions = subscriptions,
        followers = followers
    )
}

private fun PartnerDto.toPartner(): Partner {
    return Partner(
        id = id,
        firstName = firstName,
        lastName = lastName
    )
}

private fun CityDto.toCity(): City {
    return City(
        name = name,
        id = id
    )
}

fun PostWithAttachmentsAndOwner.toPost(): Post {
    return Post(
        id = data.id,
        videos = videos.map { it.toVideoExtended() },
        audios = audios.map { it.toAudio() },
        photos = photos.map { it.toPhoto() },
        date = parseToString(data.dateUnixTime),
        likes = likes.toLikes(),
        postOwner = owner.toPostOwner()
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

private fun VideoWithFirstFrames.toVideoExtended(): VideoExtended {
    return VideoExtended(
        duration = video.duration,
        firstFrame = firstFrames.map { it.toFirstFrame() },
        id = video.id,
        ownerId = video.ownerId,
        title = video.title,
        playerUrl = video.playerUrl
    )
}

private fun FirstFrameEntity.toFirstFrame(): FirstFrame {
    return FirstFrame(
        url = url,
        width = width,
        height = height
    )
}

private fun PhotoDto.toPhoto(): Photo {
    return Photo(
        albumId = albumId,
        id = id,
        ownerId = ownerId,
        sizes = sizes.map { it.toSize() },
    )
}

private fun PhotoWithSizes.toPhoto(): Photo {
    return Photo(
        ownerId = photo.ownerId,
        albumId = photo.albumId,
        id = photo.id,
        sizes = sizes.map { it.toSize() },
    )
}

private fun PostLikesEntity.toLikes(): Likes {
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
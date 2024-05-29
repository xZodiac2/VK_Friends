package com.ilya.profileViewDomain

import com.ilya.core.appCommon.enums.FriendStatus
import com.ilya.core.appCommon.enums.Relation
import com.ilya.core.appCommon.enums.Sex
import com.ilya.data.local.database.AttachmentDatabaseDto
import com.ilya.data.local.database.AudioDatabaseDto
import com.ilya.data.local.database.LikesDatabaseDto
import com.ilya.data.local.database.PhotoDatabaseDto
import com.ilya.data.local.database.PostEntity
import com.ilya.data.local.database.PostOwnerDatabaseDto
import com.ilya.data.local.database.SizeDatabaseDto
import com.ilya.data.local.database.VideoExtendedDatabaseDto
import com.ilya.data.network.retrofit.api.CityDto
import com.ilya.data.network.retrofit.api.CountersDto
import com.ilya.data.network.retrofit.api.PartnerDto
import com.ilya.data.network.retrofit.api.UserDto
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun UserDto.toUser(): User {
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
        counters = counters?.toCounters()
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

fun PostEntity.toPost(): Post {
    return Post(
        id = id,
        attachments = attachments.items.map { it.toAttachment() },
        date = parseToString(dateUnixTime),
        likes = likes.toLikes(),
        postOwner = owner.toPostOwner()
    )
}

private fun PostOwnerDatabaseDto.toPostOwner(): PostOwner {
    return PostOwner(
        id = id,
        firstName = firstName,
        lastName = lastName,
        photoUrl = photoUrl
    )
}

private fun AttachmentDatabaseDto.toAttachment(): Attachment {
    return Attachment(
        type = type,
        audio = audio?.toAudio(),
        video = video?.toVideoExtended(),
        photo = photo?.toPhoto(),
    )
}

private fun AudioDatabaseDto.toAudio(): Audio {
    return Audio(
        artist = artist,
        id = id,
        ownerId = ownerId,
        title = title,
        duration = duration,
        url = url,
    )
}

private fun VideoExtendedDatabaseDto.toVideoExtended(): VideoExtended {
    return VideoExtended(
        duration = duration,
        firstFrame = firstFrame?.map { it.toPhoto() },
        id = id,
        ownerId = ownerId,
        title = title,
        playerUrl = playerUrl
    )
}

private fun PhotoDatabaseDto.toPhoto(): Photo {
    return Photo(
        albumId = albumId,
        id = id,
        ownerId = ownerId,
        sizes = sizes?.map { it.toSize() }
    )
}

private fun SizeDatabaseDto.toSize(): Size {
    return Size(
        type = type,
        height = height,
        width = width,
        url = url
    )
}

private fun LikesDatabaseDto.toLikes(): Likes {
    return Likes(
        count = count,
        userLikes = userLikes
    )
}

private fun parseToString(dateUnixTime: Long): String {
    val date = Date(dateUnixTime)
    val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    return formatter.format(date)
}
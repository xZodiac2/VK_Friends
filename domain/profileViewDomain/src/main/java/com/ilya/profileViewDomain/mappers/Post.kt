package com.ilya.profileViewDomain.mappers

import com.ilya.core.appCommon.enums.PhotoSize
import com.ilya.data.paging.User
import com.ilya.data.paging.Video
import com.ilya.data.remote.retrofit.api.dto.LikesDto
import com.ilya.data.remote.retrofit.api.dto.PhotoDto
import com.ilya.data.remote.retrofit.api.dto.SizeDto
import com.ilya.profileViewDomain.models.Audio
import com.ilya.profileViewDomain.models.FirstFrame
import com.ilya.profileViewDomain.models.Group
import com.ilya.profileViewDomain.models.Likes
import com.ilya.profileViewDomain.models.Photo
import com.ilya.profileViewDomain.models.Post
import com.ilya.profileViewDomain.models.PostOwner
import com.ilya.profileViewDomain.models.RepostedPost
import com.ilya.profileViewDomain.models.Size
import com.ilya.profileViewDomain.models.VideoExtended
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun com.ilya.data.paging.Post.toPost(): Post {
    return Post(
        id = id,
        videos = videos.map { it.toVideoExtended() },
        audios = audios.map { it.toAudio() },
        photos = photos.map { it.toPhoto() },
        date = parseToString(dateUnixTime),
        likes = likes.toLikes(),
        owner = owner.toPostOwner(),
        text = text,
        reposted = reposted?.toRepostedPost()
    )
}

private fun com.ilya.data.paging.RepostedPost.toRepostedPost(): RepostedPost {
    return RepostedPost(
        videos = videos.map { it.toVideoExtended() },
        photos = photos.map { it.toPhoto() },
        audios = audios.map { it.toAudio() },
        owner = owner?.toPostOwner(),
        group = group?.toGroup(),
        id = id,
        text = text,
        repostedByGroup = repostedByGroup
    )
}

private fun com.ilya.data.paging.Group.toGroup(): Group {
    return Group(
        id = id,
        name = name,
        photoUrl = photoUrl
    )
}

private fun User.toPostOwner(): PostOwner {
    return PostOwner(
        id = id,
        firstName = firstName,
        lastName = lastName,
        photoUrl = photoUrl
    )
}

private fun com.ilya.data.paging.Audio.toAudio(): Audio {
    return Audio(
        artist = artist,
        id = id,
        ownerId = ownerId,
        title = title,
        duration = duration,
        url = url,
    )
}

private fun Video.toVideoExtended(): VideoExtended {
    return VideoExtended(
        duration = duration,
        firstFrame = firstFrame.map { it.toFirstFrame() },
        id = id,
        ownerId = ownerId,
        title = title,
        playerUrl = playerUrl,
        likes = likes?.toLikes()
    )
}

private fun com.ilya.data.paging.FirstFrame.toFirstFrame(): FirstFrame {
    return FirstFrame(
        url = url,
        width = width,
        height = height
    )
}

fun PhotoDto.toPhoto(): Photo {
    return Photo(
        albumId = albumId,
        id = id,
        ownerId = ownerId,
        sizes = sizes.map { it.toSize() },
        likes = likes?.toLikes(),
        accessKey = accessKey
    )
}

private fun LikesDto.toLikes(): Likes {
    return Likes(
        count = count,
        userLikes = userLikes == 1
    )
}

private fun com.ilya.data.paging.Photo.toPhoto(): Photo {
    return Photo(
        ownerId = ownerId,
        albumId = albumId,
        id = id,
        sizes = sizes.map { it.toSize() },
        likes = likes?.toLikes(),
        accessKey = accessKey
    )
}

private fun com.ilya.data.paging.Likes.toLikes(): Likes {
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

private fun com.ilya.data.paging.Size.toSize(): Size {
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
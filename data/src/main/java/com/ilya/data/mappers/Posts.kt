package com.ilya.data.mappers

import com.ilya.data.paging.Audio
import com.ilya.data.paging.Group
import com.ilya.data.paging.Post
import com.ilya.data.paging.RepostedPost
import com.ilya.data.remote.retrofit.api.dto.AudioDto
import com.ilya.data.remote.retrofit.api.dto.GroupDto
import com.ilya.data.remote.retrofit.api.dto.HistoryPostDto
import com.ilya.data.remote.retrofit.api.dto.PhotoDto
import com.ilya.data.remote.retrofit.api.dto.PostDto
import com.ilya.data.remote.retrofit.api.dto.UserDto
import com.ilya.data.remote.retrofit.api.dto.VideoAdditionalData

private const val AUDIO_TYPE = "audio"

fun PostDto.toPost(
    videos: List<VideoAdditionalData>,
    photos: List<PhotoDto>,
    owner: UserDto,
    reposted: RepostedPost? = null
): Post {

    return Post(
        id = id,
        dateUnixTime = dateUnixTime,
        text = text,
        photos = photos.map { it.toPhoto() },
        videos = videos.map { it.toVideo() },
        audios = attachments.mapNotNull { attachment ->
            if (attachment.type == AUDIO_TYPE) {
                attachment.audio?.toAudio()
            } else {
                null
            }
        },
        author = owner.toUser(),
        likes = likes.toLikes(),
        reposted = reposted,
        ownerId = ownerId
    )
}

fun HistoryPostDto.toRepostedPost(
    videos: List<VideoAdditionalData>,
    photos: List<PhotoDto>,
    owner: UserDto?,
    group: GroupDto?,
): RepostedPost {
    return RepostedPost(
        videos = videos.map { it.toVideo() },
        photos = photos.map { it.toPhoto() },
        audios = attachments.mapNotNull { attachment ->
            if (attachment.type == AUDIO_TYPE) {
                attachment.audio?.toAudio()
            } else {
                null
            }
        },
        owner = owner?.toUser(),
        group = group?.toGroup(),
        id = id,
        text = text,
        repostedByGroup = group != null
    )
}

private fun GroupDto.toGroup(): Group {
    return Group(
        id = id,
        name = name,
        photoUrl = photoUrl
    )
}

private fun AudioDto.toAudio(): Audio {
    return Audio(
        id = id,
        artist = artist,
        ownerId = ownerId,
        title = title,
        url = url,
        duration = duration
    )
}




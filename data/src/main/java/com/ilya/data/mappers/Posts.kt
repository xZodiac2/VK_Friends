package com.ilya.data.mappers

import com.ilya.core.appCommon.enums.AttachmentType
import com.ilya.data.paging.Audio
import com.ilya.data.paging.Group
import com.ilya.data.paging.Post
import com.ilya.data.paging.RepostedPost
import com.ilya.data.remote.retrofit.api.dto.AudioDto
import com.ilya.data.remote.retrofit.api.dto.GroupDto
import com.ilya.data.remote.retrofit.api.dto.HistoryPostDto
import com.ilya.data.remote.retrofit.api.dto.PostDto
import com.ilya.data.remote.retrofit.api.dto.UserDto


fun PostDto.toPost(
    owner: UserDto,
    reposted: RepostedPost? = null
): Post {
    val videos = attachments.mapNotNull { attachment ->
        if (attachment.type == AttachmentType.VIDEO.value) {
            attachment.video?.toVideo()
        } else {
            null
        }
    }
    val photos = attachments.mapNotNull { attachment ->
        if (attachment.type == AttachmentType.PHOTO.value) {
            attachment.photo?.toPhoto()
        } else {
            null
        }
    }
    val audios = attachments.mapNotNull { attachment ->
        if (attachment.type == AttachmentType.AUDIO.value) {
            attachment.audio?.toAudio()
        } else {
            null
        }
    }

    return Post(
        id = id,
        dateUnixTime = dateUnixTime,
        text = text,
        photos = photos,
        videos = videos,
        audios = audios,
        author = owner.toUser(),
        likes = likes.toLikes(),
        reposted = reposted,
        ownerId = ownerId
    )
}

fun HistoryPostDto.toRepostedPost(
    owner: UserDto?,
    group: GroupDto?,
): RepostedPost {
    val videos = attachments.mapNotNull { attachment ->
        if (attachment.type == AttachmentType.VIDEO.value) {
            attachment.video?.toVideo()
        } else {
            null
        }
    }
    val photos = attachments.mapNotNull { attachment ->
        if (attachment.type == AttachmentType.PHOTO.value) {
            attachment.photo?.toPhoto()
        } else {
            null
        }
    }
    val audios = attachments.mapNotNull { attachment ->
        if (attachment.type == AttachmentType.AUDIO.value) {
            attachment.audio?.toAudio()
        } else {
            null
        }
    }

    return RepostedPost(
        videos = videos,
        photos = photos,
        audios = audios,
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




package com.ilya.paging.mappers

import com.ilya.core.appCommon.enums.AttachmentType
import com.ilya.core.appCommon.parseUnixTimeToString
import com.ilya.data.retrofit.api.dto.AudioDto
import com.ilya.data.retrofit.api.dto.GroupDto
import com.ilya.data.retrofit.api.dto.HistoryPostDto
import com.ilya.data.retrofit.api.dto.PostDto
import com.ilya.data.retrofit.api.dto.UserDto
import com.ilya.paging.Post


fun PostDto.toPost(
    owner: UserDto,
    reposted: com.ilya.paging.RepostedPost? = null
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
        date = parseUnixTimeToString(dateUnixTime),
        text = text,
        photos = photos,
        videos = videos,
        audios = audios,
        author = owner.toPostAuthor(),
        likes = likes.toLikes(),
        reposted = reposted,
        ownerId = ownerId
    )
}

fun HistoryPostDto.toRepostedPost(
    owner: UserDto?,
    group: GroupDto?,
): com.ilya.paging.RepostedPost {
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

    return com.ilya.paging.RepostedPost(
        videos = videos,
        photos = photos,
        audios = audios,
        owner = owner?.toPostAuthor(),
        group = group?.toGroup(),
        id = id,
        text = text,
        repostedByGroup = group != null
    )
}

private fun GroupDto.toGroup(): com.ilya.paging.Group {
    return com.ilya.paging.Group(
        id = id,
        name = name,
        photoUrl = photoUrl
    )
}

private fun AudioDto.toAudio(): com.ilya.paging.Audio {
    return com.ilya.paging.Audio(
        id = id,
        artist = artist,
        ownerId = ownerId,
        title = title,
        url = url,
        duration = duration
    )
}


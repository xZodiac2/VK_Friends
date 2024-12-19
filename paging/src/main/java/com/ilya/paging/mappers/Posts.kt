package com.ilya.paging.mappers

import com.ilya.core.appCommon.enums.AttachmentType
import com.ilya.core.appCommon.parseUnixTime
import com.ilya.data.retrofit.api.dto.AudioDto
import com.ilya.data.retrofit.api.dto.CommentsInfoDto
import com.ilya.data.retrofit.api.dto.GroupDto
import com.ilya.data.retrofit.api.dto.HistoryPostDto
import com.ilya.data.retrofit.api.dto.PostDto
import com.ilya.data.retrofit.api.dto.UserDto
import com.ilya.paging.models.Audio
import com.ilya.paging.models.CommentsInfo
import com.ilya.paging.models.Group
import com.ilya.paging.models.Post
import com.ilya.paging.models.RepostedPost


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
        date = parseUnixTime(dateUnixTime),
        text = text,
        photos = photos,
        videos = videos,
        audios = audios,
        author = owner.toPostAuthor(),
        likes = likes.toLikes(),
        reposted = reposted,
        ownerId = ownerId,
        commentsInfo = commentsInfo.toCommentInfo()
    )
}

private fun CommentsInfoDto.toCommentInfo(): CommentsInfo {
    return CommentsInfo(
        count = count,
        canComment = canPost == 1,
        canView = canView == 1
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
        owner = owner?.toPostAuthor(),
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


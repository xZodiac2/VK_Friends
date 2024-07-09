package com.ilya.paging.mappers

import com.ilya.data.retrofit.api.dto.VideoDto
import com.ilya.data.retrofit.api.dto.VideoExtendedDto
import com.ilya.paging.VideoExtended

fun VideoDto.toVideo(): com.ilya.paging.Video {
    return com.ilya.paging.Video(
        duration = duration,
        id = id,
        ownerId = ownerId,
        title = title,
        firstFrame = firstFrame.map { it.toFirstFrame() },
        accessKey = accessKey
    )
}

fun VideoExtendedDto.toVideoExtended(): VideoExtended {
    return VideoExtended(
        duration = duration,
        firstFrame = firstFrame.map { it.toFirstFrame() },
        id = id,
        ownerId = ownerId,
        likes = likes.toLikes(),
        title = title,
        playerUrl = playerUrl
    )
}

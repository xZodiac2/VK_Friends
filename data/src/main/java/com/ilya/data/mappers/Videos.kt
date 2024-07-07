package com.ilya.data.mappers

import com.ilya.data.paging.Video
import com.ilya.data.remote.retrofit.api.dto.VideoDto

fun VideoDto.toVideo(): Video {
    return Video(
        duration = duration,
        id = id,
        ownerId = ownerId,
        title = title,
        firstFrame = firstFrame.map { it.toFirstFrame() },
        accessKey = accessKey
    )
}

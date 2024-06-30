package com.ilya.data.mappers

import com.ilya.data.paging.Video
import com.ilya.data.remote.retrofit.api.dto.VideoAdditionalData

fun VideoAdditionalData.toVideo(): Video {
    return Video(
        duration = duration,
        id = id,
        ownerId = ownerId,
        title = title,
        playerUrl = playerUrl,
        firstFrame = firstFrame.map { it.toFirstFrame() },
        likes = likes?.toLikes()
    )
}

package com.ilya.paging.mappers

import com.ilya.data.retrofit.api.dto.VideoDto
import com.ilya.data.retrofit.api.dto.VideoExtendedDto
import com.ilya.paging.models.Video
import com.ilya.paging.models.VideoExtended

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

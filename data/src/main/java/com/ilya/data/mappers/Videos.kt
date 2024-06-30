package com.ilya.data.mappers

import com.ilya.data.local.database.entities.FirstFrameEntity
import com.ilya.data.local.database.entities.VideoEntity
import com.ilya.data.remote.retrofit.api.dto.VideoAdditionalData
import com.ilya.data.remote.retrofit.api.dto.VideoExtendedDataDto

private fun VideoAdditionalData.toVideoEntity(postId: Long): VideoEntity {
    return VideoEntity(
        postId = postId,
        duration = duration,
        id = id,
        ownerId = ownerId,
        title = title,
        playerUrl = playerUrl
    )
}

fun VideoAdditionalData.toVideoWithFirstFrames(postId: Long): Pair<VideoEntity, List<FirstFrameEntity>> {
    return this.toVideoEntity(postId) to firstFrame.map { it.toFirstFrameEntity(this.id) }
}
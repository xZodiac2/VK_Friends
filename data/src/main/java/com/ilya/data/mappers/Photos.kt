package com.ilya.data.mappers

import com.ilya.data.local.database.entities.FirstFrameEntity
import com.ilya.data.local.database.entities.PhotoEntity
import com.ilya.data.local.database.entities.SizeEntity
import com.ilya.data.remote.retrofit.api.dto.FirstFrameDto
import com.ilya.data.remote.retrofit.api.dto.PhotoDto


fun PhotoDto.toPhotoWithSizes(postId: Long): Pair<PhotoEntity, List<SizeEntity>> {
    return this.toPhotoEntity(postId) to sizes.map { it.toSizeEntity(id) }
}

fun FirstFrameDto.toFirstFrameEntity(videoId: Long): FirstFrameEntity {
    return FirstFrameEntity(
        videoId = videoId,
        url = url,
        width = width,
        height = height
    )
}

private fun PhotoDto.toPhotoEntity(postId: Long): PhotoEntity {
    return PhotoEntity(
        albumId = albumId,
        id = id,
        ownerId = ownerId,
        postId = postId
    )
}
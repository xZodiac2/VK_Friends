package com.ilya.data.mappers

import com.ilya.data.paging.FirstFrame
import com.ilya.data.paging.Photo
import com.ilya.data.remote.retrofit.api.dto.FirstFrameDto
import com.ilya.data.remote.retrofit.api.dto.PhotoDto



fun FirstFrameDto.toFirstFrame(): FirstFrame {
    return FirstFrame(
        url = url,
        width = width,
        height = height
    )
}

fun PhotoDto.toPhoto(): Photo {
    return Photo(
        albumId = albumId,
        id = id,
        ownerId = ownerId,
        likes = likes?.toLikes(),
        sizes = sizes.map { it.toSize() }
    )
}
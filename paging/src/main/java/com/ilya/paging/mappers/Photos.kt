package com.ilya.paging.mappers

import com.ilya.data.retrofit.api.dto.FirstFrameDto
import com.ilya.data.retrofit.api.dto.PhotoDto
import com.ilya.paging.FirstFrame
import com.ilya.paging.Photo


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
        sizes = sizes.map { it.toSize() },
        accessKey = accessKey
    )
}
package com.ilya.paging.mappers

import com.ilya.core.appCommon.enums.PhotoSize
import com.ilya.data.retrofit.api.dto.FirstFrameDto
import com.ilya.data.retrofit.api.dto.PhotoDto
import com.ilya.data.retrofit.api.dto.SizeDto
import com.ilya.paging.models.FirstFrame
import com.ilya.paging.models.Photo
import com.ilya.paging.models.Size


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

fun SizeDto.toSize(): Size {
    return Size(
        type = PhotoSize.entries.find { it.value == type } ?: PhotoSize.NOT_STATED,
        height = height,
        width = width,
        url = url,
    )
}
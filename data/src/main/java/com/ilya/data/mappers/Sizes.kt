package com.ilya.data.mappers

import com.ilya.data.local.database.entities.SizeEntity
import com.ilya.data.remote.retrofit.api.dto.SizeDto



fun SizeDto.toSizeEntity(photoId: Long): SizeEntity {
    return SizeEntity(
        type = type,
        height = height,
        width = width,
        url = url,
        photoId = photoId
    )
}
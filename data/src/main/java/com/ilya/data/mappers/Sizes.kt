package com.ilya.data.mappers

import com.ilya.data.paging.Size
import com.ilya.data.remote.retrofit.api.dto.SizeDto



fun SizeDto.toSize(): Size {
    return Size(
        type = type,
        height = height,
        width = width,
        url = url,
    )
}
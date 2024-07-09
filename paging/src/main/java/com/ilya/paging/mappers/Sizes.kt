package com.ilya.paging.mappers

import com.ilya.core.appCommon.enums.PhotoSize
import com.ilya.data.retrofit.api.dto.SizeDto


fun SizeDto.toSize(): com.ilya.paging.Size {
    return com.ilya.paging.Size(
        type = PhotoSize.entries.find { it.value == type } ?: PhotoSize.NOT_STATED,
        height = height,
        width = width,
        url = url,
    )
}
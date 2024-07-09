package com.ilya.paging.mappers

import com.ilya.data.retrofit.api.dto.LikesDto
import com.ilya.paging.Likes


fun LikesDto.toLikes(): Likes {
    return Likes(
        count = count,
        userLikes = userLikes == 1
    )
}

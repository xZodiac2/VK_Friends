package com.ilya.data.mappers

import com.ilya.data.paging.Likes
import com.ilya.data.remote.retrofit.api.dto.LikesDto


fun LikesDto.toLikes(): Likes {
    return Likes(
        count = count,
        userLikes = userLikes == 1
    )
}

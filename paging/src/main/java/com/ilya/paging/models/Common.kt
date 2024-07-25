package com.ilya.paging.models

import com.ilya.core.appCommon.enums.PhotoSize

interface Likeable {
    val id: Long
    val ownerId: Long
    val likes: Likes?
}

data class Likes(
    val count: Int,
    val userLikes: Boolean
)

data class Size(
    val type: PhotoSize,
    val height: Int,
    val width: Int,
    val url: String
)

data class FirstFrame(
    val url: String,
    val width: Int,
    val height: Int
)


fun Likes.toggled(): Likes {
    return this.copy(
        userLikes = !this.userLikes,
        count = if (this.userLikes) this.count - 1 else this.count + 1
    )
}

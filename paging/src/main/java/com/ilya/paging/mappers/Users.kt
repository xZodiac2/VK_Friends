package com.ilya.paging.mappers

import com.ilya.data.retrofit.api.dto.UserDto
import com.ilya.paging.PostAuthor
import com.ilya.paging.User
import com.vk.id.AccessToken
import com.vk.id.VKIDUser

fun VKIDUser.toUser(accessToken: AccessToken): User {
    return User(
        id = accessToken.userID,
        firstName = firstName,
        lastName = lastName,
        photoUrl = photo200 ?: "",
        isClosed = false
    )
}

fun UserDto.toUser(): User {
    return User(
        id = id,
        firstName = firstName,
        lastName = lastName,
        photoUrl = photoUrl,
        isClosed = isClosed
    )
}

fun UserDto.toPostAuthor(): PostAuthor {
    return PostAuthor(
        id = id,
        firstName = firstName,
        lastName = lastName,
        photoUrl = photoUrl
    )
}
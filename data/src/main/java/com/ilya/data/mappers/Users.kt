package com.ilya.data.mappers

import com.ilya.data.paging.User
import com.ilya.data.remote.retrofit.api.dto.UserDto
import com.vk.id.AccessToken
import com.vk.id.VKIDUser

fun VKIDUser.toUser(accessToken: AccessToken): User {
    return User(
        id = accessToken.userID,
        firstName = firstName,
        lastName = lastName,
        photoUrl = photo200 ?: ""
    )
}

fun UserDto.toUser(): User {
    return User(
        id = id,
        firstName = firstName,
        lastName = lastName,
        photoUrl = photoUrl
    )
}

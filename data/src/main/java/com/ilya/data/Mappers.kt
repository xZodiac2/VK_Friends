package com.ilya.data

import com.ilya.data.local.database.FriendEntity
import com.ilya.data.local.database.UserEntity
import com.ilya.data.network.retrofit.UserDto
import com.ilya.data.paging.User
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

fun UserDto.toFriendEntity(): FriendEntity {
    return FriendEntity(
        id = id,
        firstName = firstName,
        lastName = lastName,
        photoUrl = photoUrl
    )
}

fun UserDto.toUserEntity(): UserEntity {
    return UserEntity(
        id = id,
        firstName = firstName,
        lastName = lastName,
        photoUrl = photoUrl
    )
}


fun UserEntity.toUser(): User {
    return User(
        id = id,
        firstName = firstName,
        lastName = lastName,
        photoUrl = photoUrl
    )
}

fun FriendEntity.toUser(): User {
    return User(
        id = id,
        firstName = firstName,
        lastName = lastName,
        photoUrl = photoUrl
    )
}
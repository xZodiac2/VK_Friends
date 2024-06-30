package com.ilya.data.mappers

import com.ilya.data.local.database.entities.FriendPagingEntity
import com.ilya.data.local.database.entities.PostOwnerEntity
import com.ilya.data.local.database.entities.UserData
import com.ilya.data.local.database.entities.UserPagingEntity
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

fun UserDto.toFriendEntity(): FriendPagingEntity {
    return FriendPagingEntity(
        data = UserData(
            id = id,
            firstName = firstName,
            lastName = lastName,
            photoUrl = photoUrl
        )
    )
}

fun UserDto.toUserEntity(): UserPagingEntity {
    return UserPagingEntity(
        data = UserData(
            id = id,
            firstName = firstName,
            lastName = lastName,
            photoUrl = photoUrl
        )
    )
}

fun UserPagingEntity.toUser(): User = with(data) {
    return User(
        id = id,
        firstName = firstName,
        lastName = lastName,
        photoUrl = photoUrl
    )
}

fun FriendPagingEntity.toUser(): User = with(data) {
    return User(
        id = id,
        firstName = firstName,
        lastName = lastName,
        photoUrl = photoUrl
    )
}

fun UserDto.toPostOwnerEntity(postId: Long): PostOwnerEntity {
    return PostOwnerEntity(
        postId = postId,
        data = UserData(
            id = id,
            firstName = firstName,
            lastName = lastName,
            photoUrl = photoUrl
        )
    )
}
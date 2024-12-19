package com.ilya.data

import com.ilya.core.appCommon.enums.ObjectType

typealias LikesCount = Int

interface LikesRemoteRepository {

    suspend fun addLike(
        accessToken: String,
        type: ObjectType,
        ownerId: Long,
        itemId: Long,
    ): LikesCount

    suspend fun deleteLike(
        accessToken: String,
        type: ObjectType,
        ownerId: Long,
        itemId: Long,
    ): LikesCount

}
package com.ilya.data.remote.repository

import com.ilya.core.appCommon.enums.ObjectType
import com.ilya.data.remote.LikesCount
import com.ilya.data.remote.LikesRemoteRepository
import com.ilya.data.remote.retrofit.api.LikesVkApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import javax.inject.Inject

internal class LikesVkRepository @Inject constructor(
    retrofit: Retrofit
) : LikesRemoteRepository {

    private val api = retrofit.create(LikesVkApi::class.java)

    override suspend fun addLike(
        accessToken: String,
        type: ObjectType,
        ownerId: Long,
        itemId: Long,
    ): LikesCount {
        return withContext(Dispatchers.IO) {
            api.addLike(accessToken, type.value, ownerId, itemId)
        }.response.count
    }

    override suspend fun deleteLike(
        accessToken: String,
        type: ObjectType,
        ownerId: Long,
        itemId: Long,
    ): LikesCount {
        return withContext(Dispatchers.IO) {
            api.deleteLike(accessToken, type.value, ownerId, itemId)
        }.response.count
    }
}
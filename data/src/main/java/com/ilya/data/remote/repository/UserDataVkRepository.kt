package com.ilya.data.remote.repository

import com.ilya.core.appCommon.enums.NameCase
import com.ilya.data.remote.UserDataRemoteRepository
import com.ilya.data.remote.retrofit.api.UserDataVkApi
import com.ilya.data.remote.retrofit.api.dto.PostDto
import com.ilya.data.remote.retrofit.api.dto.UserDto
import com.ilya.data.remote.retrofit.api.dto.VideoExtendedDataDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Inject

internal class UserDataVkRepository @Inject constructor(
    retrofit: Retrofit
) : UserDataRemoteRepository {

    private val userDataVkApi = retrofit.create<UserDataVkApi>()

    override suspend fun getUser(
        accessToken: String,
        userId: Long,
        nameCase: NameCase,
        fields: List<String>
    ): UserDto {
        return withContext(Dispatchers.IO) {
            userDataVkApi.getUserData(
                accessToken = accessToken,
                userId = userId,
                fields = fields.joinToString(","),
                nameCase = nameCase.value
            )
        }.response.first()
    }

    override suspend fun getWall(
        accessToken: String,
        ownerId: Long,
        count: Int,
        offset: Int
    ): List<PostDto> {
        return withContext(Dispatchers.IO) {
            userDataVkApi.getWall(
                accessToken = accessToken,
                ownerId = ownerId,
                offset = offset,
                count = count
            )
        }.response.items
    }

    override suspend fun getVideoData(
        accessToken: String,
        ownerId: Long,
        videoId: String,
    ): VideoExtendedDataDto {
        return withContext(Dispatchers.IO) {
            userDataVkApi.getVideoData(
                accessToken = accessToken,
                ownerId = ownerId,
                videoId = videoId
            )
        }.response.items.first()
    }

}
package com.ilya.data.network.repository

import com.ilya.core.appCommon.enums.NameCase
import com.ilya.data.network.UserDataRemoteRepository
import com.ilya.data.network.retrofit.api.UserDataVkApi
import com.ilya.data.network.retrofit.api.UserDto
import com.ilya.data.network.retrofit.api.VideoExtendedDto
import com.ilya.data.network.retrofit.api.WallItemDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class UserDataVkRepository @Inject constructor(
    private val userDataVkApi: UserDataVkApi
) : UserDataRemoteRepository {

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
    ): List<WallItemDto> {
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
        videoId: Long,
        accessKey: String
    ): VideoExtendedDto {
        return withContext(Dispatchers.IO) {
            userDataVkApi.getVideoData(
                accessToken = accessToken,
                ownerId = ownerId,
                videoId = "${ownerId}_${videoId}" + if (accessKey.isNotEmpty()) "_$accessKey" else ""
            )
        }
    }

}
package com.ilya.data.remote.repository

import com.ilya.core.appCommon.enums.NameCase
import com.ilya.data.remote.UserDataRemoteRepository
import com.ilya.data.remote.retrofit.api.UserDataVkApi
import com.ilya.data.remote.retrofit.api.dto.PhotoDto
import com.ilya.data.remote.retrofit.api.dto.PhotosResponseData
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

    private val api = retrofit.create<UserDataVkApi>()

    override suspend fun getUser(
        accessToken: String,
        userId: Long,
        nameCase: NameCase,
        fields: List<String>
    ): UserDto {
        return withContext(Dispatchers.IO) {
            api.getUserData(
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
            api.getWall(
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
            api.getVideoData(
                accessToken = accessToken,
                ownerId = ownerId,
                videoId = videoId
            )
        }.response.items.first()
    }

    override suspend fun getPhotos(
        accessToken: String,
        ownerId: Long,
        extended: Boolean,
        offset: Int,
        count: Int
    ): PhotosResponseData {
        return withContext(Dispatchers.IO) {
            api.getPhotos(
                accessToken = accessToken,
                ownerId = ownerId,
                extended = extended.toInt(),
                offset = offset,
                count = count
            )
        }.response
    }

    override suspend fun getPhotos(accessToken: String, photoIds: List<String>): List<PhotoDto> {
        return withContext(Dispatchers.IO) {
            api.getPhotos(
                accessToken = accessToken,
                photoIds = photoIds.joinToString(",")
            ).response
        }
    }

    private fun Boolean.toInt(): Int {
        return if (this) 1 else 0
    }

}
package com.ilya.data.remote

import com.ilya.core.appCommon.enums.NameCase
import com.ilya.data.remote.retrofit.api.dto.PhotoDto
import com.ilya.data.remote.retrofit.api.dto.PhotosResponseData
import com.ilya.data.remote.retrofit.api.dto.PostDto
import com.ilya.data.remote.retrofit.api.dto.UserDto
import com.ilya.data.remote.retrofit.api.dto.VideoExtendedDataDto

interface UserDataRemoteRepository {

    suspend fun getUser(
        accessToken: String,
        userId: Long,
        nameCase: NameCase = NameCase.NOMINATIVE,
        fields: List<String>
    ): UserDto

    suspend fun getWall(
        accessToken: String,
        ownerId: Long,
        count: Int,
        offset: Int
    ): List<PostDto>

    suspend fun getVideoData(
        accessToken: String,
        ownerId: Long,
        videoId: String,
    ): VideoExtendedDataDto

    suspend fun getPhotos(
        accessToken: String,
        ownerId: Long,
        extended: Boolean,
        offset: Int,
        count: Int
    ): PhotosResponseData

    suspend fun getPhotos(
        accessToken: String,
        photoIds: List<String>
    ): List<PhotoDto>

}
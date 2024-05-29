package com.ilya.data.network

import com.ilya.core.appCommon.enums.NameCase
import com.ilya.data.network.retrofit.api.PostDto
import com.ilya.data.network.retrofit.api.UserDto
import com.ilya.data.network.retrofit.api.VideoExtendedDataDto

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

}
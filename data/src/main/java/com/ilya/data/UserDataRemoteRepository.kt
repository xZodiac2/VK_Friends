package com.ilya.data

import com.ilya.core.appCommon.enums.NameCase
import com.ilya.data.retrofit.api.dto.PhotoDto
import com.ilya.data.retrofit.api.dto.PhotosResponseData
import com.ilya.data.retrofit.api.dto.UserDto
import com.ilya.data.retrofit.api.dto.VideoExtendedDto

interface UserDataRemoteRepository {

  suspend fun getUser(
    accessToken: String,
    userId: Long,
    nameCase: NameCase = NameCase.NOMINATIVE,
    fields: List<String>
  ): UserDto

  suspend fun getVideoData(
    accessToken: String,
    ownerId: Long,
    videoId: String,
  ): VideoExtendedDto

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

  suspend fun getVideo(
    accessToken: String,
    videoId: String
  ): VideoExtendedDto

}
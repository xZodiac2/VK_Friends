package com.ilya.data.repository

import com.ilya.core.appCommon.enums.NameCase
import com.ilya.data.UserDataRemoteRepository
import com.ilya.data.retrofit.api.UserDataVkApi
import com.ilya.data.retrofit.api.dto.PhotoDto
import com.ilya.data.retrofit.api.dto.PhotosResponseData
import com.ilya.data.retrofit.api.dto.UserDto
import com.ilya.data.retrofit.api.dto.VideoExtendedDto
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

  override suspend fun getVideoData(
    accessToken: String,
    ownerId: Long,
    videoId: String,
  ): VideoExtendedDto {
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

  override suspend fun getVideo(accessToken: String, videoId: String): VideoExtendedDto {
    val ownerId = videoId.substringBefore("_").toLong()

    return withContext(Dispatchers.IO) {
      api.getVideo(accessToken, ownerId, videoId)
    }.response.items.first()
  }

  private fun Boolean.toInt(): Int {
    return if (this) 1 else 0
  }

}
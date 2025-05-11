package com.ilya.data.repository

import com.ilya.data.WallRemoteRepository
import com.ilya.data.retrofit.api.WallVkApi
import com.ilya.data.retrofit.api.dto.CommentsResponseData
import com.ilya.data.retrofit.api.dto.PostDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import javax.inject.Inject

internal class WallVkRepository @Inject constructor(
  retrofit: Retrofit
) : WallRemoteRepository {

  private val api = retrofit.create(WallVkApi::class.java)

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

  override suspend fun getComments(
    accessToken: String,
    ownerId: Long,
    postId: Long,
    needLikes: Int,
    offset: Int,
    count: Int,
    extended: Int,
    fields: String,
    threadCount: Int
  ): CommentsResponseData {
    return withContext(Dispatchers.IO) {
      api.getComments(accessToken, ownerId, postId, needLikes, offset, count, extended, fields, threadCount)
    }.response
  }
}
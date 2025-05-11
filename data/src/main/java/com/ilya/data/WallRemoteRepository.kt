package com.ilya.data

import com.ilya.data.retrofit.api.dto.CommentsResponseData
import com.ilya.data.retrofit.api.dto.PostDto

interface WallRemoteRepository {

  suspend fun getWall(
    accessToken: String,
    ownerId: Long,
    count: Int,
    offset: Int
  ): List<PostDto>

  suspend fun getComments(
    accessToken: String,
    ownerId: Long,
    postId: Long,
    needLikes: Int,
    offset: Int,
    count: Int,
    extended: Int,
    fields: String,
    threadCount: Int = 10
  ): CommentsResponseData

}
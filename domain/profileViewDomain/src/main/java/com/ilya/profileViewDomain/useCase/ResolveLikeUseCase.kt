package com.ilya.profileViewDomain.useCase

import com.ilya.core.appCommon.base.UseCase
import com.ilya.data.LikesRemoteRepository
import com.ilya.paging.models.LikeableCommonInfo
import com.ilya.paging.models.Likes
import javax.inject.Inject

class ResolveLikeUseCase @Inject constructor(
  private val repository: LikesRemoteRepository
) : UseCase<ResolveLikeUseCase.InvokeData, Result<Likes>> {

  override suspend fun invoke(data: InvokeData): Result<Likes> {
    val likes = data.info.likes ?: return Result.failure(IllegalArgumentException())

    val likesCount = if (likes.userLikes) {
      repository.deleteLike(
        accessToken = data.accessToken,
        type = data.info.objectType,
        ownerId = data.info.ownerId,
        itemId = data.info.id
      )
    } else {
      repository.addLike(
        accessToken = data.accessToken,
        type = data.info.objectType,
        ownerId = data.info.ownerId,
        itemId = data.info.id
      )
    }

    return Result.success(Likes(likesCount, !likes.userLikes))
  }

  data class InvokeData(
    val info: LikeableCommonInfo,
    val accessToken: String
  )

}



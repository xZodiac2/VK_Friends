package com.ilya.profileViewDomain.useCase

import com.ilya.core.appCommon.base.UseCase
import com.ilya.data.LikesRemoteRepository
import com.ilya.paging.models.Likeable
import com.ilya.paging.models.Likes
import javax.inject.Inject

class ResolveLikeUseCase @Inject constructor(
    private val repository: LikesRemoteRepository
) : UseCase<ResolveLikeUseCase.InvokeData, Result<Likes>> {

    override suspend fun invoke(data: InvokeData): Result<Likes> {
        val likes = data.likeable.likes ?: return Result.failure(IllegalArgumentException())

        val likesCount = if (likes.userLikes) {
            repository.deleteLike(
                accessToken = data.accessToken,
                type = data.likeable.objectType,
                ownerId = data.likeable.ownerId,
                itemId = data.likeable.id
            )
        } else {
            repository.addLike(
                accessToken = data.accessToken,
                type = data.likeable.objectType,
                ownerId = data.likeable.ownerId,
                itemId = data.likeable.id
            )
        }

        return Result.success(
            Likes(
                count = likesCount,
                userLikes = !likes.userLikes
            )
        )
    }

    data class InvokeData(
        val likeable: Likeable,
        val accessToken: String
    )

}



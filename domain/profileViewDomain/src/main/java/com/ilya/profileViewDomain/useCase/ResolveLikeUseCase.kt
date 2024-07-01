package com.ilya.profileViewDomain.useCase

import com.ilya.core.appCommon.UseCase
import com.ilya.core.appCommon.enums.ObjectType
import com.ilya.data.remote.LikesRemoteRepository
import com.ilya.profileViewDomain.models.Likeable
import com.ilya.profileViewDomain.models.Likes
import com.ilya.profileViewDomain.models.Photo
import com.ilya.profileViewDomain.models.Post
import com.ilya.profileViewDomain.models.VideoExtended
import javax.inject.Inject

class ResolveLikeUseCase @Inject constructor(
    private val repository: LikesRemoteRepository
) : UseCase<ResolveLikeUseCaseInvokeData, Result<Likes>> {
    override suspend fun invoke(data: ResolveLikeUseCaseInvokeData): Result<Likes> {
        val likes = data.likeable.likes ?: return Result.failure(IllegalArgumentException())
        val type = when (data.likeable) {
            is Photo -> ObjectType.PHOTO
            is VideoExtended -> ObjectType.VIDEO
            is Post -> ObjectType.POST
            else -> return Result.failure(IllegalArgumentException())
        }

        val likesCount = if (likes.userLikes) {
            repository.deleteLike(
                accessToken = data.accessToken,
                type = type,
                ownerId = data.likeable.ownerId,
                itemId = data.likeable.id
            )
        } else {
            repository.addLike(
                accessToken = data.accessToken,
                type = type,
                ownerId = data.likeable.ownerId,
                itemId = data.likeable.id
            )
        }

        return Result.success(Likes(
            count = likesCount,
            userLikes = !likes.userLikes
        ))
    }
}

data class ResolveLikeUseCaseInvokeData(
    val likeable: Likeable,
    val accessToken: String
)


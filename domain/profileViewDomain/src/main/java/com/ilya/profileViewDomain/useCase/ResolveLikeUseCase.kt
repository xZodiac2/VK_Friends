package com.ilya.profileViewDomain.useCase

import com.ilya.core.appCommon.UseCase
import com.ilya.core.appCommon.enums.ObjectType
import com.ilya.data.LikesRemoteRepository
import com.ilya.paging.Likeable
import com.ilya.paging.Likes
import com.ilya.paging.Photo
import com.ilya.paging.Post
import com.ilya.paging.VideoExtended
import javax.inject.Inject

class ResolveLikeUseCase @Inject constructor(
    private val repository: LikesRemoteRepository
) : UseCase<ResolveLikeUseCase.InvokeData, Result<Likes>> {

    override suspend fun invoke(data: InvokeData): Result<Likes> {
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

    data class InvokeData(
        val likeable: Likeable,
        val accessToken: String
    )

}



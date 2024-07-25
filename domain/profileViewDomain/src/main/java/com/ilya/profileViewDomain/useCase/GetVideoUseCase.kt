package com.ilya.profileViewDomain.useCase

import com.ilya.core.appCommon.base.UseCase
import com.ilya.data.UserDataRemoteRepository
import com.ilya.paging.mappers.toVideoExtended
import com.ilya.paging.models.VideoExtended
import javax.inject.Inject

class GetVideoUseCase @Inject constructor(
    private val remoteRepository: UserDataRemoteRepository
) : UseCase<GetVideoUseCase.InvokeData, VideoExtended> {

    override suspend fun invoke(data: InvokeData): VideoExtended {
        val fullId = "${data.ownerId}_${data.videoId}_${data.accessKey}"

        return remoteRepository.getVideo(data.accessToken, fullId).toVideoExtended()
    }

    data class InvokeData(
        val accessToken: String,
        val ownerId: Long,
        val videoId: Long,
        val accessKey: String
    )

}

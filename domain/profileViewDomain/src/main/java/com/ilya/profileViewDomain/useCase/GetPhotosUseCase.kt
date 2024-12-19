package com.ilya.profileViewDomain.useCase

import com.ilya.core.appCommon.base.UseCase
import com.ilya.data.UserDataRemoteRepository
import com.ilya.paging.mappers.toPhoto
import com.ilya.paging.models.Photo
import javax.inject.Inject

class GetPhotosUseCase @Inject constructor(
    private val userDataRemoteRepository: UserDataRemoteRepository
) : UseCase<GetPhotosUseCase.InvokeData, List<Photo>> {

    override suspend fun invoke(data: InvokeData): List<Photo> {
        val fullIds = combineFullIds(data.userId, data.photoIds)
        val photos = userDataRemoteRepository.getPhotos(data.accessToken, fullIds)
        return photos.map { it.toPhoto() }
    }

    private fun combineFullIds(userId: Long, photoIds: Map<Long, String>): List<String> {
        return photoIds.keys.map {
            val accessKey = photoIds[it] ?: ""
            if (accessKey.isNotBlank()) {
                "${userId}_${it}_${accessKey}"
            } else {
                "${userId}_${it}"
            }
        }
    }

    data class InvokeData(
        val accessToken: String,
        val userId: Long,
        val photoIds: Map<Long, String>
    )

}

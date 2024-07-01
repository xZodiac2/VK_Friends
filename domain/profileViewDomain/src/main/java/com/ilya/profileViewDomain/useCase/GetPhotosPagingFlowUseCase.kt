package com.ilya.profileViewDomain.useCase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.ilya.core.appCommon.UseCase
import com.ilya.data.paging.pagingSources.PhotosPagingSource
import com.ilya.profileViewDomain.mappers.toPhoto
import com.ilya.profileViewDomain.models.Photo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetPhotosPagingFlowUseCase @Inject constructor(
    private val photosPagingSourceFactory: PhotosPagingSource.Factory
) : UseCase<GetPhotosPagingFlowUseCaseInvokeData, Flow<PagingData<Photo>>> {

    override suspend fun invoke(data: GetPhotosPagingFlowUseCaseInvokeData): Flow<PagingData<Photo>> {
        // initialKey calculate example: (targetPhotoIndex = 82, pageSize = 20) -> 4
        val initialKey = (data.targetPhotoIndex?.minus(data.targetPhotoIndex % data.pagingConfig.pageSize))?.div(data.pagingConfig.pageSize)

        return Pager(
            config = data.pagingConfig,
            initialKey = initialKey,
            pagingSourceFactory = {
                val initData = PhotosPagingSource.InitData(
                    userId = data.userId,
                    isPreview = data.isPreview
                )

                photosPagingSourceFactory.newInstance(initData)
            }
        ).flow.map { it.map { photo -> photo.toPhoto() } }
    }

}

data class GetPhotosPagingFlowUseCaseInvokeData(
    val pagingConfig: PagingConfig,
    val userId: Long,
    val isPreview: Boolean,
    val targetPhotoIndex: Int? = null
)

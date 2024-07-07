package com.ilya.profileViewDomain.useCase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.ilya.core.appCommon.UseCase
import com.ilya.data.paging.pagingSources.PostsPagingSource
import com.ilya.profileViewDomain.mappers.toPost
import com.ilya.profileViewDomain.models.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetPostsPagingFlowUseCase @Inject constructor(
    private val postsPagingSourceFactory: PostsPagingSource.Factory
) : UseCase<GetPostsPagingFlowUseCase.InvokeData, Flow<PagingData<Post>>> {

    override suspend fun invoke(data: InvokeData): Flow<PagingData<Post>> {
        return Pager(
            config = data.config,
            pagingSourceFactory = { postsPagingSourceFactory.newInstance(data.userId) },
        ).flow.map { it.map { postEntity -> postEntity.toPost() } }
    }

    data class InvokeData(
        val config: PagingConfig,
        val userId: Long
    )


}


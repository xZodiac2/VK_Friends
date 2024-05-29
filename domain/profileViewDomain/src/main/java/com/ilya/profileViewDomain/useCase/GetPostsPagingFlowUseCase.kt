package com.ilya.profileViewDomain.useCase

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.ilya.core.appCommon.UseCase
import com.ilya.data.paging.pagingSources.PostsPagingSourceFactory
import com.ilya.data.paging.remoteMediators.PostsRemoteMediator
import com.ilya.profileViewDomain.Post
import com.ilya.profileViewDomain.toPost
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetPostsPagingFlowUseCase @Inject constructor(
    private val pagingSourceFactory: PostsPagingSourceFactory,
    private val remoteMediatorFactory: PostsRemoteMediator.Factory
) : UseCase<GetPostsPagingFlowUseCaseInvokeData, Flow<PagingData<Post>>> {

    @OptIn(ExperimentalPagingApi::class)
    override suspend fun invoke(data: GetPostsPagingFlowUseCaseInvokeData): Flow<PagingData<Post>> {
        return Pager(
            config = data.config,
            pagingSourceFactory = { pagingSourceFactory.newInstance(Unit) },
            remoteMediator = remoteMediatorFactory.newInstance(data.userId)
        ).flow.map { it.map { postEntity -> postEntity.toPost() } }
    }
}

data class GetPostsPagingFlowUseCaseInvokeData(
    val config: PagingConfig,
    val userId: Long
)

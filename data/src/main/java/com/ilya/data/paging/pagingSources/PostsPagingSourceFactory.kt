package com.ilya.data.paging.pagingSources

import androidx.paging.PagingSource
import com.ilya.core.appCommon.BaseFactory
import com.ilya.data.local.LocalRepository
import com.ilya.data.local.database.PostEntity
import javax.inject.Inject

class PostsPagingSourceFactory @Inject constructor(
    private val localRepository: LocalRepository<PostEntity>
) : BaseFactory<Unit, PagingSource<Int, PostEntity>> {
    override fun newInstance(initializationData: Unit): PagingSource<Int, PostEntity> {
        return localRepository.getPagingSource()
    }
}
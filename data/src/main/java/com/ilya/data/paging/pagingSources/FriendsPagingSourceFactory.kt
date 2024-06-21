package com.ilya.data.paging.pagingSources

import androidx.paging.PagingSource
import com.ilya.core.appCommon.BaseFactory
import com.ilya.data.local.LocalRepository
import com.ilya.data.local.database.entities.FriendPagingEntity
import javax.inject.Inject

class FriendsPagingSourceFactory @Inject constructor(
    private val localRepository: LocalRepository<FriendPagingEntity>
) : BaseFactory<Unit, PagingSource<Int, FriendPagingEntity>> {

    override fun newInstance(initializationData: Unit): PagingSource<Int, FriendPagingEntity> {
        return localRepository.getPagingSource()
    }
}
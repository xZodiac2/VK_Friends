package com.ilya.data.paging.pagingSources

import androidx.paging.PagingSource
import com.ilya.core.appCommon.BaseFactory
import com.ilya.data.local.LocalRepository
import com.ilya.data.local.database.FriendEntity
import javax.inject.Inject

class FriendsPagingSourceFactory @Inject constructor(
    private val localRepository: LocalRepository<FriendEntity>
) : BaseFactory<Unit, PagingSource<Int, FriendEntity>> {

    override fun newInstance(initializationData: Unit): PagingSource<Int, FriendEntity> {
        return localRepository.getPagingSource()
    }
}
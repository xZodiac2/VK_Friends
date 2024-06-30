package com.ilya.data.paging.pagingSources.factories

import androidx.paging.PagingSource
import com.ilya.core.appCommon.BaseFactory
import com.ilya.data.local.LocalRepository
import com.ilya.data.local.database.entities.UserPagingEntity
import javax.inject.Inject

class UsersPagingSourceFactory @Inject constructor(
    private val localRepository: LocalRepository<UserPagingEntity>
) : BaseFactory<Unit, PagingSource<Int, UserPagingEntity>> {

    override fun newInstance(initializationData: Unit): PagingSource<Int, UserPagingEntity> {
        return localRepository.getPagingSource()
    }
}
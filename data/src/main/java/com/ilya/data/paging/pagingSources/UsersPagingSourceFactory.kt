package com.ilya.data.paging.pagingSources

import androidx.paging.PagingSource
import com.ilya.core.appCommon.BaseFactory
import com.ilya.data.local.LocalRepository
import com.ilya.data.local.database.UserEntity
import javax.inject.Inject

class UsersPagingSourceFactory @Inject constructor(
    private val localRepository: LocalRepository<UserEntity>
) : BaseFactory<Unit, PagingSource<Int, UserEntity>> {
    override fun newInstance(initializationData: Unit): PagingSource<Int, UserEntity> {
        return localRepository.getAll()
    }
}
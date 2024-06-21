package com.ilya.data.paging.pagingSources

import androidx.paging.PagingSource
import com.ilya.core.appCommon.BaseFactory
import com.ilya.data.local.LocalRepository
import com.ilya.data.local.database.entities.PostWithAttachmentsAndOwner
import javax.inject.Inject

class PostsPagingSourceFactory @Inject constructor(
    private val localRepository: LocalRepository<PostWithAttachmentsAndOwner>
) : BaseFactory<Unit, PagingSource<Int, PostWithAttachmentsAndOwner>> {
    override fun newInstance(initializationData: Unit): PagingSource<Int, PostWithAttachmentsAndOwner> {
        return localRepository.getPagingSource()
    }
}
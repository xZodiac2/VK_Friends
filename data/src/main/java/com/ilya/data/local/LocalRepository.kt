package com.ilya.data.local

import androidx.paging.PagingSource

interface LocalRepository<T : Any> {

    suspend fun upsertAll(vararg upsertData: T)

    fun getAll(): PagingSource<Int, T>

    suspend fun deleteAll()

    suspend fun deleteAllWithPrimaryKeys()

    suspend fun withTransaction(block: suspend () -> Unit)

}
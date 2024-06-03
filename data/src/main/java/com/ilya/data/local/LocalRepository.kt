package com.ilya.data.local

import androidx.paging.PagingSource

interface LocalRepository<T : Any> {

    suspend fun upsertAll(vararg upsertData: T)

    fun getPagingSource(): PagingSource<Int, T>

    suspend fun getAll(): List<T>

    suspend fun deleteAll()

    suspend fun deleteAllWithPrimaryKeys()

    suspend fun withTransaction(block: suspend () -> Unit)

}
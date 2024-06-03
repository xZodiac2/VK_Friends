package com.ilya.data.local.repository

import androidx.paging.PagingSource
import androidx.room.withTransaction
import com.ilya.data.local.LocalRepository
import com.ilya.data.local.database.PostEntity
import com.ilya.data.local.database.VkFriendsApplicationDatabase
import javax.inject.Inject

internal class PostsLocalRepository @Inject constructor(
    private val database: VkFriendsApplicationDatabase
) : LocalRepository<PostEntity> {

    override suspend fun upsertAll(vararg upsertData: PostEntity) {
        database.postsDao.upsertAll(upsertData.toList())
    }

    override fun getPagingSource(): PagingSource<Int, PostEntity> {
        return database.postsDao.getPagingSource()
    }

    override suspend fun getAll(): List<PostEntity> {
        return database.postsDao.getAll()
    }

    override suspend fun deleteAll() {
        database.postsDao.deleteAll()
    }

    override suspend fun deleteAllWithPrimaryKeys() {
        database.postsDao.deleteAll()
        database.postsDao.deletePrimaryKeys()
    }

    override suspend fun withTransaction(block: suspend () -> Unit) {
        database.withTransaction(block)
    }

}
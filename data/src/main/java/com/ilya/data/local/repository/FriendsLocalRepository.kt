package com.ilya.data.local.repository

import androidx.paging.PagingSource
import androidx.room.withTransaction
import com.ilya.data.local.LocalRepository
import com.ilya.data.local.database.VkFriendsApplicationDatabase
import com.ilya.data.local.database.entities.FriendPagingEntity
import javax.inject.Inject

internal class FriendsLocalRepository @Inject constructor(
    private val database: VkFriendsApplicationDatabase
) : LocalRepository<FriendPagingEntity> {

    override suspend fun upsertAll(vararg upsertData: FriendPagingEntity) {
        database.friendsDao.upsertAll(upsertData.toList())
    }

    override fun getPagingSource(): PagingSource<Int, FriendPagingEntity> {
        return database.friendsDao.getPagingSource()
    }

    override suspend fun getAll(): List<FriendPagingEntity> {
        return database.friendsDao.getAll()
    }

    override suspend fun deleteAll() {
        database.friendsDao.deleteAll()
    }

    override suspend fun deleteAllWithPrimaryKeys() {
        database.friendsDao.deleteAll()
        database.friendsDao.deletePrimaryKeys()
    }

    override suspend fun withTransaction(block: suspend () -> Unit) {
        database.withTransaction(block)
    }

}
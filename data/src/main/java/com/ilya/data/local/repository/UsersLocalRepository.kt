package com.ilya.data.local.repository

import androidx.paging.PagingSource
import androidx.room.withTransaction
import com.ilya.data.local.LocalRepository
import com.ilya.data.local.database.VkFriendsApplicationDatabase
import com.ilya.data.local.database.entities.UserPagingEntity
import javax.inject.Inject

internal class UsersLocalRepository @Inject constructor(
    private val database: VkFriendsApplicationDatabase
) : LocalRepository<UserPagingEntity> {

    override suspend fun upsertAll(vararg upsertData: UserPagingEntity) {
        database.usersDao.upsertAll(upsertData.toList())
    }

    override fun getPagingSource(): PagingSource<Int, UserPagingEntity> {
        return database.usersDao.getPagingSource()
    }

    override suspend fun getAll(): List<UserPagingEntity> {
        return database.usersDao.getAll()
    }

    override suspend fun deleteAll() {
        database.usersDao.deleteAll()
    }

    override suspend fun deleteAllWithPrimaryKeys() {
        database.usersDao.deleteAll()
        database.usersDao.deletePrimaryKeys()
    }

    override suspend fun withTransaction(block: suspend () -> Unit) {
        database.withTransaction(block)
    }

}
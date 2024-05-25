package com.ilya.data.local.repository

import androidx.paging.PagingSource
import com.ilya.data.local.LocalRepository
import com.ilya.data.local.database.UserEntity
import com.ilya.data.local.database.VkFriendsApplicationDatabase
import javax.inject.Inject

internal class UsersLocalRepository @Inject constructor(
    private val database: VkFriendsApplicationDatabase
) : LocalRepository<UserEntity> {

    override suspend fun upsertAll(vararg upsertData: UserEntity) {
        database.usersDao.upsertAll(upsertData.toList())
    }

    override fun getAll(): PagingSource<Int, UserEntity> {
        return database.usersDao.getPagingSource()
    }

    override suspend fun deleteAll() {
        database.usersDao.deleteAll()
    }

    override suspend fun deleteAllWithPrimaryKeys() {
        database.usersDao.deleteAll()
        database.usersDao.deletePrimaryKeys()
    }

}
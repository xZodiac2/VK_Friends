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
        database.dao.upsertAllUsers(upsertData.toList())
    }

    override fun getAll(): PagingSource<Int, UserEntity> {
        return database.dao.getUsersPagingSource()
    }

    override suspend fun deleteAll() {
        database.dao.deleteAllFromUsers()
    }

    override suspend fun deleteAllWithPrimaryKeys() {
        database.dao.deleteAllFromUsers()
        database.dao.deleteUsersPrimaryKeyIndex()
    }

}
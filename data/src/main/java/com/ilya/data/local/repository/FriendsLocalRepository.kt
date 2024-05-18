package com.ilya.data.local.repository

import androidx.paging.PagingSource
import com.ilya.data.local.LocalRepository
import com.ilya.data.local.database.FriendEntity
import com.ilya.data.local.database.VkFriendsApplicationDatabase
import javax.inject.Inject

internal class FriendsLocalRepository @Inject constructor(
    private val database: VkFriendsApplicationDatabase
) : LocalRepository<FriendEntity> {

    override suspend fun upsertAll(vararg upsertData: FriendEntity) {
        database.dao.upsertAllFriends(upsertData.toList())
    }

    override fun getAll(): PagingSource<Int, FriendEntity> {
        return database.dao.getFriendsPagingSource()
    }

    override suspend fun deleteAll() {
        database.dao.deleteAllFromFriends()
    }

    override suspend fun deleteAllWithPrimaryKeys() {
        database.dao.deleteAllFromFriends()
        database.dao.deleteFriendsPrimaryKeyIndex()
    }

}
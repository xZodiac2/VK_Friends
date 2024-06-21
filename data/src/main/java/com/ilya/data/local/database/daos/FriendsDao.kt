package com.ilya.data.local.database.daos

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ilya.data.local.database.entities.FriendPagingEntity

@Dao
internal interface FriendsDao {

    @Upsert
    suspend fun upsertAll(friends: List<FriendPagingEntity>)

    @Query("SELECT * FROM friends_paging")
    fun getPagingSource(): PagingSource<Int, FriendPagingEntity>

    @Query("SELECT * FROM friends_paging")
    suspend fun getAll(): List<FriendPagingEntity>

    @Query("DELETE FROM friends_paging")
    suspend fun deleteAll()

    @Query("DELETE FROM sqlite_sequence WHERE name = 'friends_paging'")
    suspend fun deletePrimaryKeys()

}
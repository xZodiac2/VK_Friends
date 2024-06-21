package com.ilya.data.local.database.daos

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ilya.data.local.database.entities.UserPagingEntity

@Dao
internal interface UsersDao {

    @Upsert
    suspend fun upsertAll(users: List<UserPagingEntity>)

    @Query("SELECT * FROM users_paging")
    fun getPagingSource(): PagingSource<Int, UserPagingEntity>

    @Query("SELECT * FROM users_paging")
    suspend fun getAll(): List<UserPagingEntity>

    @Query("DELETE FROM users_paging")
    suspend fun deleteAll()

    @Query("DELETE FROM sqlite_sequence WHERE name = 'users_paging'")
    suspend fun deletePrimaryKeys()

}
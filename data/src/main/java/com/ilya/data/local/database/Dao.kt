package com.ilya.data.local.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
internal interface Dao {

    @Upsert
    suspend fun upsertAllFriends(friends: List<FriendEntity>)

    @Upsert
    suspend fun upsertAllUsers(users: List<UserEntity>)

    @Query("SELECT * FROM friends_table")
    fun getFriendsPagingSource(): PagingSource<Int, FriendEntity>

    @Query("SELECT * FROM users_table")
    fun getUsersPagingSource(): PagingSource<Int, UserEntity>

    @Query("DELETE FROM friends_table")
    suspend fun deleteAllFromFriends()

    @Query("DELETE FROM users_table")
    suspend fun deleteAllFromUsers()

    @Query("DELETE FROM sqlite_sequence WHERE name = 'users_table'")
    suspend fun deleteUsersPrimaryKeyIndex()

    @Query("DELETE FROM sqlite_sequence WHERE name = 'friends_table'")
    suspend fun deleteFriendsPrimaryKeyIndex()

}

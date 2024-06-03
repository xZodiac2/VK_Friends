package com.ilya.data.local.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
internal interface FriendsDao {

    @Upsert
    suspend fun upsertAll(friends: List<FriendEntity>)

    @Query("SELECT * FROM friends_table")
    fun getPagingSource(): PagingSource<Int, FriendEntity>

    @Query("SELECT * FROM friends_table")
    suspend fun getAll(): List<FriendEntity>

    @Query("DELETE FROM friends_table")
    suspend fun deleteAll()

    @Query("DELETE FROM sqlite_sequence WHERE name = 'friends_table'")
    suspend fun deletePrimaryKeys()

}

@Dao
internal interface UsersDao {

    @Upsert
    suspend fun upsertAll(users: List<UserEntity>)

    @Query("SELECT * FROM users_table")
    fun getPagingSource(): PagingSource<Int, UserEntity>

    @Query("SELECT * FROM users_table")
    suspend fun getAll(): List<UserEntity>

    @Query("DELETE FROM users_table")
    suspend fun deleteAll()

    @Query("DELETE FROM sqlite_sequence WHERE name = 'users_table'")
    suspend fun deletePrimaryKeys()

}

@Dao
internal interface PostsDao {

    @Upsert
    suspend fun upsertAll(posts: List<PostEntity>)

    @Query("SELECT * FROM posts_table ORDER BY databaseId")
    fun getPagingSource(): PagingSource<Int, PostEntity>

    @Query("SELECT * FROM posts_table")
    suspend fun getAll(): List<PostEntity>

    @Query("DELETE FROM posts_table")
    suspend fun deleteAll()

    @Query("DELETE FROM sqlite_sequence WHERE name = 'posts_table'")
    suspend fun deletePrimaryKeys()

}
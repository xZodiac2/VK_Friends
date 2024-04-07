package com.ilya.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "friends_table")
data class FriendEntity(
    @PrimaryKey(autoGenerate = true)
    val databaseId: Int = 0,
    val id: Long,
    val firstName: String,
    val lastName: String,
    val photoUrl: String
)

@Entity(tableName = "users_table")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val databaseId: Int = 0,
    val id: Long,
    val firstName: String,
    val lastName: String,
    val photoUrl: String
)
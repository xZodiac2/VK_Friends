package com.ilya.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FriendEntity::class, UserEntity::class], version = 1)
internal abstract class VkFriendsApplicationDatabase : RoomDatabase() {
    abstract val dao: Dao
}


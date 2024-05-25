package com.ilya.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        FriendEntity::class,
        UserEntity::class,
        WallItemEntity::class
    ],
    version = 1,
)
internal abstract class VkFriendsApplicationDatabase : RoomDatabase() {
    abstract val friendsDao: FriendsDao
    abstract val usersDao: UsersDao
}


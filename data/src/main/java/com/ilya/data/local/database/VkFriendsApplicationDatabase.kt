package com.ilya.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ilya.data.local.database.converters.AttachmentsConverter
import com.ilya.data.local.database.converters.LikesConverter
import com.ilya.data.local.database.converters.PostOwnerConverter

@Database(
    entities = [
        FriendEntity::class,
        UserEntity::class,
        PostEntity::class
    ],
    version = 1,
)
@TypeConverters(LikesConverter::class, AttachmentsConverter::class, PostOwnerConverter::class)
internal abstract class VkFriendsApplicationDatabase : RoomDatabase() {
    abstract val friendsDao: FriendsDao
    abstract val usersDao: UsersDao
    abstract val postsDao: PostsDao
}


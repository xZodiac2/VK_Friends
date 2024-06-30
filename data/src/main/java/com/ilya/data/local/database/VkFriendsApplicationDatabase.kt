package com.ilya.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ilya.data.local.database.daos.FriendsDao
import com.ilya.data.local.database.daos.PostsDao
import com.ilya.data.local.database.daos.UsersDao
import com.ilya.data.local.database.entities.AudioEntity
import com.ilya.data.local.database.entities.FirstFrameEntity
import com.ilya.data.local.database.entities.FriendPagingEntity
import com.ilya.data.local.database.entities.PhotoEntity
import com.ilya.data.local.database.entities.PhotoLikesEntity
import com.ilya.data.local.database.entities.PostLikesEntity
import com.ilya.data.local.database.entities.PostOwnerEntity
import com.ilya.data.local.database.entities.PostPagingEntity
import com.ilya.data.local.database.entities.SizeEntity
import com.ilya.data.local.database.entities.UserPagingEntity
import com.ilya.data.local.database.entities.VideoEntity
import com.ilya.data.local.database.entities.VideoLikesEntity

@Database(
    entities = [
        FriendPagingEntity::class,
        UserPagingEntity::class,
        PostPagingEntity::class,
        PhotoEntity::class,
        VideoEntity::class,
        AudioEntity::class,
        SizeEntity::class,
        FirstFrameEntity::class,
        PostOwnerEntity::class,
        PostLikesEntity::class,
        VideoLikesEntity::class,
        PhotoLikesEntity::class,
    ],
    version = 1,
    exportSchema = false
)
internal abstract class VkFriendsApplicationDatabase : RoomDatabase() {
    abstract val friendsDao: FriendsDao
    abstract val usersDao: UsersDao
    abstract val postsDao: PostsDao
}


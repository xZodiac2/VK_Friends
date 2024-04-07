package com.ilya.data.local.database

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): VkFriendsApplicationDatabase {
        return Room.databaseBuilder(
            context = context,
            klass = VkFriendsApplicationDatabase::class.java,
            name = "vkFriendsApplicationDatabase.db"
        ).build()
    }

}

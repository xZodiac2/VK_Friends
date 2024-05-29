package com.ilya.data.local.database.di

import android.content.Context
import androidx.room.Room
import com.ilya.data.local.database.VkFriendsApplicationDatabase
import com.ilya.data.local.database.converters.AttachmentsConverter
import com.ilya.data.local.database.converters.LikesConverter
import com.ilya.data.local.database.converters.PostOwnerConverter
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
    fun provideDatabase(
        @ApplicationContext context: Context,
        likesConverter: LikesConverter,
        attachmentsConverter: AttachmentsConverter,
        postOwnerConverter: PostOwnerConverter
    ): VkFriendsApplicationDatabase {
        return Room.databaseBuilder(
            context = context,
            klass = VkFriendsApplicationDatabase::class.java,
            name = "vkFriendsApplicationDatabase.db"
        )
            .addTypeConverter(likesConverter)
            .addTypeConverter(attachmentsConverter)
            .addTypeConverter(postOwnerConverter)
            .build()
    }

}

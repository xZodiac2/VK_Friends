package com.ilya.data.local.database.di

import com.ilya.data.local.database.Attachment
import com.ilya.data.local.database.Likes
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@OptIn(ExperimentalStdlibApi::class)
@Module
@InstallIn(SingletonComponent::class)
internal object JsonAdaptersModule {

    @Singleton
    @Provides
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    fun provideLikesJsonAdapter(moshi: Moshi): JsonAdapter<Likes> = moshi.adapter()

    @Provides
    fun provideAttachmentsJsonAdapter(moshi: Moshi): JsonAdapter<List<Attachment>> = moshi.adapter()

}
package com.ilya.friendsview.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Named

@Module
@InstallIn(ViewModelComponent::class)
object AccessTokenProvider {
    @Provides
    @Named("accessToken")
    fun provideAccessToken(@ApplicationContext context: Context): String {
        return context.getSharedPreferences("VKFriendsAppSharedPreferences", Context.MODE_PRIVATE)
            .getString("accessToken", "") ?: ""
    }
}
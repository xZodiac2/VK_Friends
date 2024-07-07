package com.ilya.core.commonDi

import android.media.AudioAttributes
import android.media.MediaPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MediaPlayerModule {

    @Singleton
    @Provides
    fun provideMediaPlayer(): MediaPlayer {
        val player = MediaPlayer()
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()

        player.setAudioAttributes(audioAttributes)
        return player
    }

}
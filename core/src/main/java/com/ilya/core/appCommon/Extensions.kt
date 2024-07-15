package com.ilya.core.appCommon

import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.paging.compose.LazyPagingItems

fun <T : Any> LazyPagingItems<T>.isEmpty(): Boolean {
    return this.itemCount == 0
}

fun MediaPlayer.configure(): MediaPlayer {
    val audioAttributes = AudioAttributes.Builder()
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .build()
    this.setAudioAttributes(audioAttributes)
    return this
}
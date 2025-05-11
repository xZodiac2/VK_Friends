package com.ilya.core.appCommon

import android.media.AudioAttributes
import android.media.MediaPlayer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun MediaPlayer.configure(): MediaPlayer {
  val audioAttributes = AudioAttributes.Builder()
    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
    .build()
  this.setAudioAttributes(audioAttributes)
  return this
}

fun parseUnixTime(dateUnixTime: Long): String {
  val date = Date(dateUnixTime * 1000L)
  val formatter = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
  return formatter.format(date)
}


package com.ilya.profileview.photosScreen.event

internal sealed interface PhotosScreenEvent {
  data class Start(val userId: Long) : PhotosScreenEvent
}

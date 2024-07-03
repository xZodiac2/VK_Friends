package com.ilya.profileview.photosScreen

internal sealed interface PhotosScreenEvent {
    data class Start(val userId: Long) : PhotosScreenEvent
}

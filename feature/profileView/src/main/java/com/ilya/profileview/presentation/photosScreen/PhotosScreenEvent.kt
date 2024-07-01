package com.ilya.profileview.presentation.photosScreen

internal sealed interface PhotosScreenEvent {
    data class Start(val userId: Long) : PhotosScreenEvent
}

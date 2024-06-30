package com.ilya.profileview.presentation.photosScreen

sealed interface PhotosScreenEvent {
    data class Start(val userId: Long) : PhotosScreenEvent
}

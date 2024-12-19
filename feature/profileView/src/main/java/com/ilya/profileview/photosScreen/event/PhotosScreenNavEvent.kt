package com.ilya.profileview.photosScreen.event

sealed interface PhotosScreenNavEvent {
    data object BackClick : PhotosScreenNavEvent
    data class OpenPhoto(val userId: Long, val photoIndex: Int) : PhotosScreenNavEvent
    data object EmptyAccessToken : PhotosScreenNavEvent
}
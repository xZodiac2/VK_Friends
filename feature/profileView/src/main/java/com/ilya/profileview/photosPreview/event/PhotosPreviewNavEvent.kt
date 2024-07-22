package com.ilya.profileview.photosPreview.event

sealed interface PhotosPreviewNavEvent {
    data object NavigateToAuth : PhotosPreviewNavEvent
    data object BackClick : PhotosPreviewNavEvent
}
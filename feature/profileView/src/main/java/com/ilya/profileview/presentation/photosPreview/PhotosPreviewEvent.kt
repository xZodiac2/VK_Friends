package com.ilya.profileview.presentation.photosPreview

import com.ilya.profileViewDomain.models.Likes
import com.ilya.profileViewDomain.models.Photo


internal sealed interface PhotosPreviewEvent {
    data object SnackbarConsumed : PhotosPreviewEvent
    data class PhotosAdded(val likes: Map<Long, Likes>) : PhotosPreviewEvent
    data class Start(val userId: Long, val targetPhotoIndex: Int) : PhotosPreviewEvent
    data class Like(val photo: Photo?) : PhotosPreviewEvent
}
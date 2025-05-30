package com.ilya.profileview.photosPreview.event

import com.ilya.paging.models.LikeableCommonInfo
import com.ilya.paging.models.Likes


internal sealed interface PhotosPreviewEvent {
    data object SnackbarConsumed : PhotosPreviewEvent
    data class PhotosAdded(val likes: Map<Long, Likes>) : PhotosPreviewEvent
    data class Start(
        val userId: Long,
        val targetPhotoIndex: Int
    ) : PhotosPreviewEvent

    data class RestrainedStart(
        val userId: Long,
        val targetPhotoIndex: Int,
        val photoIds: Map<Long, String>
    ) : PhotosPreviewEvent

    data class Like(val photo: LikeableCommonInfo?) : PhotosPreviewEvent
    data class Error(val error: Throwable?) : PhotosPreviewEvent
}
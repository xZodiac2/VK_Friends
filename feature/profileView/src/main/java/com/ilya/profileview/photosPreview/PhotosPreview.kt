package com.ilya.profileview.photosPreview

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.ilya.profileview.photosPreview.components.AllPhotosPreview
import com.ilya.profileview.photosPreview.components.RestrainedPhotosPreview
import com.ilya.profileview.photosPreview.event.PhotosPreviewNavEvent

@Composable
fun PhotosPreview(
    userId: Long,
    targetPhotoIndex: Int,
    photoIds: Map<Long, String> = emptyMap(),
    handleNavEvent: (PhotosPreviewNavEvent) -> Unit
) {
    val viewModel: PhotosPreviewViewModel = hiltViewModel()

    BackHandler {
        handleNavEvent(PhotosPreviewNavEvent.BackClick)
    }

    if (photoIds.isEmpty()) {
        AllPhotosPreview(
            viewModel = viewModel,
            userId = userId,
            targetPhotoIndex = targetPhotoIndex,
            onBackClick = {
                handleNavEvent(PhotosPreviewNavEvent.BackClick)
            },
            navigateToAuth = { handleNavEvent(PhotosPreviewNavEvent.NavigateToAuth) }
        )
    } else {
        RestrainedPhotosPreview(
            viewModel = viewModel,
            userId = userId,
            targetPhotoIndex = targetPhotoIndex,
            photoIds = photoIds,
            onBackClick = {
                handleNavEvent(PhotosPreviewNavEvent.BackClick)
            },
            navigateToAuth = { handleNavEvent(PhotosPreviewNavEvent.NavigateToAuth) }
        )
    }

}




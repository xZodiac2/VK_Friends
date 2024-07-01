package com.ilya.profileview.presentation.photosPreview

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.ilya.profileview.presentation.photosPreview.components.AllPhotosPreview
import com.ilya.profileview.presentation.photosPreview.components.RestrainedPhotosPreview

@Composable
fun PhotosPreview(
    userId: Long,
    targetPhotoIndex: Int,
    onBackClick: () -> Unit,
    photoIds: List<Long> = emptyList(),
) {
    val viewModel: PhotosPreviewViewModel = hiltViewModel()

    if (photoIds.isEmpty()) {
        AllPhotosPreview(viewModel, onBackClick, userId, targetPhotoIndex)
    } else {
        RestrainedPhotosPreview(viewModel, onBackClick, userId, targetPhotoIndex, photoIds)
    }
}




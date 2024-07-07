package com.ilya.profileview.photosPreview

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ilya.profileview.photosPreview.components.AllPhotosPreview
import com.ilya.profileview.photosPreview.components.RestrainedPhotosPreview

@Composable
fun PhotosPreview(
    userId: Long,
    targetPhotoIndex: Int,
    onBackClick: () -> Unit,
    photoIds: Map<Long, String> = emptyMap(),
) {
    val viewModel: PhotosPreviewViewModel = hiltViewModel()
    val systemUiController = rememberSystemUiController()

    BackHandler {
        systemUiController.isStatusBarVisible = true
        onBackClick()
    }

    if (photoIds.isEmpty()) {
        AllPhotosPreview(
            viewModel = viewModel,
            userId = userId,
            targetPhotoIndex = targetPhotoIndex,
            onBackClick = {
                systemUiController.isStatusBarVisible = true
                onBackClick()
            }
        )
    } else {
        RestrainedPhotosPreview(
            viewModel = viewModel,
            userId = userId,
            targetPhotoIndex = targetPhotoIndex,
            photoIds = photoIds,
            onBackClick = {
                systemUiController.isStatusBarVisible = true
                onBackClick()
            }
        )
    }

    LaunchedEffect(Unit) {
        systemUiController.isStatusBarVisible = false
    }

}




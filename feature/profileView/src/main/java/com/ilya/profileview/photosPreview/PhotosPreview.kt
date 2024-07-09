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
    photoIds: Map<Long, String> = emptyMap(),
    onBackClick: () -> Unit,
    navigateToAuth: () -> Unit
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
            },
            navigateToAuth = navigateToAuth
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
            },
            navigateToAuth = navigateToAuth
        )
    }

    LaunchedEffect(Unit) {
        systemUiController.isStatusBarVisible = false
    }

}




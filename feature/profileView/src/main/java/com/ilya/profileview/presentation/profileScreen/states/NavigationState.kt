package com.ilya.profileview.presentation.profileScreen.states

sealed interface NavigationState {
    data object Profile : NavigationState
    data class PhotosPreview(
        val photoId: Long,
        val photosJson: List<String>
    ) : NavigationState
}
package com.ilya.profileview.presentation.profileScreen.states

import com.ilya.profileViewDomain.models.Photo


sealed interface PhotosState {
    data object NoPhotos : PhotosState
    data class Show(val photos: List<Photo?>) : PhotosState
}

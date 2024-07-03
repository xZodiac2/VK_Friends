package com.ilya.profileview.photosPreview.states

import com.ilya.profileViewDomain.models.Photo

sealed interface RestrainedPhotosState {
    data object Loading : RestrainedPhotosState
    data class ShowPhotos(val photos: List<Photo>) : RestrainedPhotosState
}

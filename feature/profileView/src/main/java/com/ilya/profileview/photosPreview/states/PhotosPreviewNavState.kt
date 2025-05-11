package com.ilya.profileview.photosPreview.states


internal sealed interface PhotosPreviewNavState {
  data object This : PhotosPreviewNavState
  data object AuthScreen : PhotosPreviewNavState
}

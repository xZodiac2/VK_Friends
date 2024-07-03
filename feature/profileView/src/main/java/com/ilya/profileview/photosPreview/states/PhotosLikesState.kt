package com.ilya.profileview.photosPreview.states

import androidx.compose.runtime.Stable
import com.ilya.profileViewDomain.models.Likes

@Stable
internal class PhotosLikesState(
    val likes: Map<Long, Likes>
)

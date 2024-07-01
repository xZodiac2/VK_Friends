package com.ilya.profileview.presentation.photosPreview

import androidx.compose.runtime.Stable
import com.ilya.profileViewDomain.models.Likes

@Stable
internal class LikesState(
    val likes: Map<Long, Likes>
)

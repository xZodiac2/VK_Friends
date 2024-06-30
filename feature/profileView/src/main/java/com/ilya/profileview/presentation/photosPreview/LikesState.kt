package com.ilya.profileview.presentation.photosPreview

import com.ilya.profileViewDomain.models.Likes

data class LikesState(
    val likes: Map<Long, Likes>
)

package com.ilya.profileview.profileScreen

import androidx.compose.runtime.Stable
import com.ilya.profileViewDomain.models.Likes
import com.ilya.profileViewDomain.models.User

@Stable
internal sealed interface ProfileScreenState {
    data object Loading : ProfileScreenState
    data class Success(val user: User) : ProfileScreenState
    data class Error(val errorType: ErrorType) : ProfileScreenState
}

@Stable
internal class PostsLikesState(
    val likes: Map<Long, Likes>
)
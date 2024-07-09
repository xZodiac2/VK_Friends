package com.ilya.profileview.profileScreen

import androidx.compose.runtime.Stable
import com.ilya.paging.Likes
import com.ilya.profileViewDomain.User

@Stable
internal sealed interface ProfileScreenState {
    data object Loading : ProfileScreenState
    data class ViewData(val user: User) : ProfileScreenState
    data class Error(val errorType: ErrorType) : ProfileScreenState
}

@Stable
internal class PostsLikesState(
    val likes: Map<Long, Likes>
)

internal sealed interface AudioLoadIndicatorState {
    data object Idle : AudioLoadIndicatorState
    data object Loading : AudioLoadIndicatorState
}
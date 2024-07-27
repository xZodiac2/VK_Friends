package com.ilya.profileview.profileScreen

import androidx.compose.runtime.Stable
import androidx.paging.PagingData
import com.ilya.paging.models.Comment
import com.ilya.paging.models.Likes
import com.ilya.profileViewDomain.User
import kotlinx.coroutines.flow.Flow

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

@Stable
internal sealed interface AudioLoadIndicatorState {
    data object Idle : AudioLoadIndicatorState
    data object Loading : AudioLoadIndicatorState
}

@Stable
internal data class CommentsBottomSheetState(
    val showSheet: Boolean,
    val commentsFlow: Flow<PagingData<Comment>>
)
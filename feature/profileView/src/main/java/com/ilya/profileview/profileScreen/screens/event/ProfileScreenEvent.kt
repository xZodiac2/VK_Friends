package com.ilya.profileview.profileScreen.screens.event

import com.ilya.paging.models.Audio
import com.ilya.paging.models.LikeableCommonInfo
import com.ilya.paging.models.Likes
import com.ilya.profileViewDomain.User

internal sealed interface ProfileScreenEvent {
    data object SnackbarConsumed : ProfileScreenEvent
    data object Retry : ProfileScreenEvent
    data object Back : ProfileScreenEvent
    data object DismissBottomSheet : ProfileScreenEvent
    data class Start(val userId: Long) : ProfileScreenEvent
    data class FriendRequest(val user: User) : ProfileScreenEvent
    data class Like(val item: LikeableCommonInfo) : ProfileScreenEvent
    data class PostsAdded(val newLikes: Map<Long, Likes>) : ProfileScreenEvent
    data class AudioClick(val audio: Audio) : ProfileScreenEvent
    data class NewNavEvent(val navEvent: ProfileScreenNavEvent) : ProfileScreenEvent
    data class CommentsClick(val postId: Long) : ProfileScreenEvent
    data class CommentsAdded(val newLikes: Map<Long, Likes>) : ProfileScreenEvent
}

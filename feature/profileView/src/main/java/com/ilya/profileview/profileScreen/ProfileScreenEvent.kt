package com.ilya.profileview.profileScreen

import com.ilya.paging.Audio
import com.ilya.paging.Likes
import com.ilya.paging.Post
import com.ilya.profileViewDomain.User


internal sealed interface ProfileScreenEvent {
    data object SnackbarConsumed : ProfileScreenEvent
    data object Retry : ProfileScreenEvent
    data object Back : ProfileScreenEvent
    data class Start(val userId: Long) : ProfileScreenEvent
    data class FriendRequest(val user: User) : ProfileScreenEvent
    data class Like(val post: Post) : ProfileScreenEvent
    data class PostsAdded(val newLikes: Map<Long, Likes>) : ProfileScreenEvent
    data class AudioClick(val audio: Audio) : ProfileScreenEvent
}

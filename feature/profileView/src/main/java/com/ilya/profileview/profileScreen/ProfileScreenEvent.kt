package com.ilya.profileview.profileScreen

import com.ilya.profileViewDomain.models.Likes
import com.ilya.profileViewDomain.models.Post
import com.ilya.profileViewDomain.models.User


internal sealed interface ProfileScreenEvent {
    data object SnackbarConsumed : ProfileScreenEvent
    data object Retry : ProfileScreenEvent
    data class Start(val userId: Long) : ProfileScreenEvent
    data class FriendRequest(val user: User) : ProfileScreenEvent
    data class Like(val post: Post) : ProfileScreenEvent
    data class PostsAdded(val newLikes: Map<Long, Likes>) : ProfileScreenEvent
}

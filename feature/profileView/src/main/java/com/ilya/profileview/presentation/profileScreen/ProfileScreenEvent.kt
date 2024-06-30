package com.ilya.profileview.presentation.profileScreen

import com.ilya.profileViewDomain.models.User


sealed interface ProfileScreenEvent {
    data object SnackbarConsumed : ProfileScreenEvent
    data object Retry : ProfileScreenEvent
    data class Start(val userId: Long) : ProfileScreenEvent
    data class FriendRequest(val user: User) : ProfileScreenEvent
}

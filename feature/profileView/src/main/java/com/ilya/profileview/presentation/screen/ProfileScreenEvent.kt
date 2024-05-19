package com.ilya.profileview.presentation.screen

import com.ilya.profileViewDomain.User


sealed interface ProfileScreenEvent {
    data object Retry : ProfileScreenEvent
    data class Start(val userId: Long) : ProfileScreenEvent
    data class FriendRequest(val user: User) : ProfileScreenEvent
}

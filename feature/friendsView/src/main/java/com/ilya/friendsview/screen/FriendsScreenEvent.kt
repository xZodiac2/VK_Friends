package com.ilya.friendsview.screen


sealed interface FriendsScreenEvent {
    object PlaceholderAvatarClick : FriendsScreenEvent
    object SnackbarConsumed : FriendsScreenEvent
    object Start : FriendsScreenEvent
    data class UnknownError(val error: Throwable) : FriendsScreenEvent
    data class BackPress(val onConfirm: () -> Unit) : FriendsScreenEvent
}

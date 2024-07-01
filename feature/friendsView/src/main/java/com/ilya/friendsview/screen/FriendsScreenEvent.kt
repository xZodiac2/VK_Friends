package com.ilya.friendsview.screen


internal sealed interface FriendsScreenEvent {
    data object PlaceholderAvatarClick : FriendsScreenEvent
    data object SnackbarConsumed : FriendsScreenEvent
    data object Start : FriendsScreenEvent
    data class BackPress(val onConfirm: () -> Unit) : FriendsScreenEvent
}

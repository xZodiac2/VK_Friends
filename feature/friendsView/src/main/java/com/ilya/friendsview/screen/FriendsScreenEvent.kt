package com.ilya.friendsview.screen


sealed interface FriendsScreenEvent {
    object Start : FriendsScreenEvent
    object Retry : FriendsScreenEvent
}
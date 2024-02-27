package com.ilya.friendsview.screen

sealed interface FiendsScreenEvent {
    object Start : FiendsScreenEvent
    object Restart : FiendsScreenEvent
}
package com.ilya.friendsview.screen.event

sealed interface FriendsScreenNavEvent {
    data object EmptyAccessToken : FriendsScreenNavEvent
    data class OpenProfile(val id: Long) : FriendsScreenNavEvent
}
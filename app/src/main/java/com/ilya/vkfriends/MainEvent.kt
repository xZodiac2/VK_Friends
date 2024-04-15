package com.ilya.vkfriends

sealed interface MainEvent {

    object EmptyAccessToken : MainEvent

}

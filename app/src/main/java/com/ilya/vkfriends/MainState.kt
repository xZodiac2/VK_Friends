package com.ilya.vkfriends

sealed interface MainState {

    object Authorized : MainState
    object NotAuthorized : MainState

}
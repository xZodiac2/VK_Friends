package com.ilya.auth.screen

sealed interface AuthorizationScreenState {
    data object NotAuthorized : AuthorizationScreenState
    data object Authorized : AuthorizationScreenState
}

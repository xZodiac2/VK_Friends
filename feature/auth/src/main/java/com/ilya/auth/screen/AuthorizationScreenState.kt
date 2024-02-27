package com.ilya.auth.screen

sealed interface AuthorizationScreenState {
    object NotAuthorized : AuthorizationScreenState
    object Authorized : AuthorizationScreenState
    object Idle : AuthorizationScreenState
}

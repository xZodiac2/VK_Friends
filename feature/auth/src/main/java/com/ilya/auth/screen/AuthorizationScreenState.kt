package com.ilya.auth.screen

import androidx.compose.runtime.Stable

@Stable
sealed interface AuthorizationScreenState {
    data object NotAuthorized : AuthorizationScreenState
    data object Authorized : AuthorizationScreenState
}

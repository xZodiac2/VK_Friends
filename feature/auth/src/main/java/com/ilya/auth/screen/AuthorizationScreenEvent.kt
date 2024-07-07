package com.ilya.auth.screen

import com.vk.id.AccessToken

internal sealed interface AuthorizationScreenEvent {
    data object Fail : AuthorizationScreenEvent
    data object SnackbarConsumed : AuthorizationScreenEvent
    data class Authorize(val accessToken: AccessToken) : AuthorizationScreenEvent
}

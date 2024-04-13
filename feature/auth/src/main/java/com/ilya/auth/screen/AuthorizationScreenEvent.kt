package com.ilya.auth.screen

import com.vk.id.AccessToken

sealed interface AuthorizationScreenEvent {
    data class Authorize(val accessToken: AccessToken) : AuthorizationScreenEvent
}

package com.ilya.auth.screen

import com.vk.id.AccessToken

sealed interface AuthorizationScreenEvent {
    object Start : AuthorizationScreenEvent
    data class Authorize(val accessToken: AccessToken) : AuthorizationScreenEvent
}

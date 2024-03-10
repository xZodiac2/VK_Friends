package com.ilya.auth

import androidx.lifecycle.ViewModel
import com.ilya.auth.screen.AuthorizationScreenEvent
import com.ilya.auth.screen.AuthorizationScreenState
import com.ilya.core.appCommon.AccessTokenManager
import com.vk.id.AccessToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AuthorizationScreenViewModel @Inject constructor(
    private val accessTokenManager: AccessTokenManager
) : ViewModel() {
    
    private val _authorizationScreenState = MutableStateFlow<AuthorizationScreenState>(AuthorizationScreenState.Idle)
    val authorizationScreenState = _authorizationScreenState.asStateFlow()
    
    fun handleEvent(event: AuthorizationScreenEvent) {
        when (event) {
            AuthorizationScreenEvent.Start -> onStart()
            is AuthorizationScreenEvent.Authorize -> onAuthorize(event.accessToken)
        }
    }
    
    private fun onStart() {
        if (accessTokenManager.accessToken == null) {
            _authorizationScreenState.value = AuthorizationScreenState.NotAuthorized
        } else {
            _authorizationScreenState.value = AuthorizationScreenState.Authorized
        }
    }
    
    private fun onAuthorize(token: AccessToken) {
        accessTokenManager.saveAccessToken(token)
        _authorizationScreenState.value = AuthorizationScreenState.Authorized
    }
    
}


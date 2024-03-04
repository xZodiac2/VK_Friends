package com.ilya.auth

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import com.ilya.auth.screen.AuthorizationScreenEvent
import com.ilya.auth.screen.AuthorizationScreenState
import com.vk.id.AccessToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AuthorizationScreenViewModel @Inject constructor(
    private val shPrefs: SharedPreferences,
) : ViewModel() {
    
    private val _authorizationScreenState = MutableStateFlow<AuthorizationScreenState>(AuthorizationScreenState.Idle)
    val authorizationScreenState = _authorizationScreenState.asStateFlow()
    
    private val accessToken = shPrefs.getString(ACCESS_TOKEN_KEY, "") ?: ""
    
    fun handleEvent(event: AuthorizationScreenEvent) {
        when (event) {
            AuthorizationScreenEvent.Start -> onStart()
            is AuthorizationScreenEvent.Authorize -> onAuthorize(event.accessToken)
        }
    }
    
    private fun onStart() {
        if (accessToken.isEmpty()) {
            _authorizationScreenState.value = AuthorizationScreenState.NotAuthorized
        } else {
            _authorizationScreenState.value = AuthorizationScreenState.Authorized
        }
    }
    
    private fun onAuthorize(token: AccessToken) {
        Log.d("mytag", "Auth")
        
        with(shPrefs.edit()) {
            putString(ACCESS_TOKEN_KEY, token.token)
            apply()
        }
        
        _authorizationScreenState.value = AuthorizationScreenState.Authorized
    }
    
    companion object {
        private const val ACCESS_TOKEN_KEY = "accessToken"
    }
}


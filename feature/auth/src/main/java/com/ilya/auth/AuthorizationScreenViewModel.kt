package com.ilya.auth

import androidx.lifecycle.ViewModel
import com.ilya.auth.screen.AuthorizationScreenEvent
import com.ilya.auth.screen.AuthorizationScreenState
import com.ilya.core.appCommon.StringResource
import com.ilya.core.appCommon.accessToken.AccessTokenManager
import com.ilya.core.appCommon.compose.basicComposables.snackbar.SnackbarState
import com.vk.id.AccessToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
internal class AuthorizationScreenViewModel @Inject constructor(
    private val accessTokenManager: AccessTokenManager
) : ViewModel() {

    private val _authorizationScreenState =
        MutableStateFlow<AuthorizationScreenState>(AuthorizationScreenState.NotAuthorized)
    val authorizationScreenState = _authorizationScreenState.asStateFlow()

    private val _snackbarState = MutableStateFlow<SnackbarState>(SnackbarState.Consumed)
    val snackbarState = _snackbarState.asStateFlow()

    fun handleEvent(event: AuthorizationScreenEvent) {
        when (event) {
            is AuthorizationScreenEvent.Authorize -> onAuthorize(event.accessToken)
            is AuthorizationScreenEvent.Fail -> onFail()
            is AuthorizationScreenEvent.SnackbarConsumed -> onSnackbarConsumed()
        }
    }

    private fun onFail() {
        _snackbarState.value = SnackbarState.Triggered(StringResource.FromId(R.string.error_auth))
    }

    private fun onSnackbarConsumed() {
        _snackbarState.value = SnackbarState.Consumed
    }

    private fun onAuthorize(token: AccessToken) {
        accessTokenManager.accessToken = token
        _authorizationScreenState.value = AuthorizationScreenState.Authorized
    }

}


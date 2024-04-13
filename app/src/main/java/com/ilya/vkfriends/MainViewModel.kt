package com.ilya.vkfriends

import androidx.lifecycle.ViewModel
import com.ilya.core.appCommon.AccessTokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val accessTokenManager: AccessTokenManager
) : ViewModel() {

    private val _mainState = MutableStateFlow<MainState>(MainState.Authorized)
    val mainState = _mainState.asStateFlow()

    fun handleEvent(mainEvent: MainEvent) {
        when (mainEvent) {
            MainEvent.Start -> onStart()
            MainEvent.EmptyAccessToken -> onEmptyAccessToken()
        }
    }

    private fun onEmptyAccessToken() {
        _mainState.value = MainState.NotAuthorized
    }

    private fun onStart() {
        if (accessTokenManager.accessToken == null) {
            _mainState.value = MainState.NotAuthorized
        }
    }

}
package com.ilya.vkfriends

import androidx.lifecycle.ViewModel
import com.ilya.core.appCommon.AccessTokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    accessTokenManager: AccessTokenManager
) : ViewModel() {

    private val _mainState = MutableStateFlow<MainState>(MainState.NotAuthorized)
    val mainState = _mainState.asStateFlow()

    init {
        if (accessTokenManager.accessToken != null) {
            _mainState.value = MainState.Authorized
        }
    }

    fun handleEvent(mainEvent: MainEvent) {
        when (mainEvent) {
            MainEvent.EmptyAccessToken -> onEmptyAccessToken()
        }
    }

    private fun onEmptyAccessToken() {
        _mainState.value = MainState.NotAuthorized
    }

}
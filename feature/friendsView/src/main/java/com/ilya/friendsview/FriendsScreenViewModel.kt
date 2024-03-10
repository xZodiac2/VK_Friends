package com.ilya.friendsview

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ilya.core.appCommon.AccessTokenManager
import com.ilya.data.VkRepository
import com.ilya.friendsview.screen.FriendsScreenEvent
import com.vk.id.AccessToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendsScreenViewModel @Inject constructor(
    private val repository: VkRepository,
    private val accessTokenManager: AccessTokenManager,
) : ViewModel() {
    
    private val _screenMutableState = MutableStateFlow<FriendsScreenState>(FriendsScreenState.Loading)
    val screenState = _screenMutableState.asStateFlow()
    
    private val _isRefreshingState = MutableStateFlow(false)
    val pullRefreshState = _isRefreshingState.asStateFlow()
    
    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        _screenMutableState.value = FriendsScreenState.Error(ErrorType.NoInternet)
        _isRefreshingState.value = false
        Log.d("mytag", "Message: " + exception.message)
        Log.d("mytag", "Cause: " + exception.cause.toString())
    }
    
    fun handleEvent(event: FriendsScreenEvent) {
        when (event) {
            is FriendsScreenEvent.Start -> onStart()
            is FriendsScreenEvent.Retry -> onRetry()
            is FriendsScreenEvent.Refresh -> onRefresh()
        }
    }
    
    private fun onRefresh() {
        val accessTokenValue = accessTokenManager.accessToken ?: return
        
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            _isRefreshingState.value = true
            getFriends(accessTokenValue)
            _isRefreshingState.value = false
        }
        
    }
    
    private fun onRetry() {
        val accessTokenValue = accessTokenManager.accessToken
        
        if (accessTokenValue == null) {
            _screenMutableState.value = FriendsScreenState.Error(ErrorType.NoAccessToken)
            return
        }
        
        _screenMutableState.value = FriendsScreenState.Loading
        
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            getFriends(accessTokenValue)
        }
    }
    
    private fun onStart() {
        if (_screenMutableState.value == FriendsScreenState.Loading || _isRefreshingState.value) {
            
            val accessTokenValue = accessTokenManager.accessToken
            if (accessTokenValue == null) {
                _screenMutableState.value = FriendsScreenState.Error(ErrorType.NoAccessToken)
                return
            }
            
            viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
                getFriends(accessTokenValue)
            }
        }
        
    }
    
    private suspend fun getFriends(accessTokenValue: AccessToken) {
        val friends = repository.getFriends(accessTokenValue.token)
        _screenMutableState.value = FriendsScreenState.Success(friends)
    }
    
}
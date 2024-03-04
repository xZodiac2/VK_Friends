package com.ilya.friendsview

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ilya.data.VkRepository
import com.ilya.friendsview.screen.FriendsScreenEvent
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
    shPrefs: SharedPreferences,
) : ViewModel() {
    
    private val _screenMutableState = MutableStateFlow<FriendsScreenState>(FriendsScreenState.Loading)
    val screenState = _screenMutableState.asStateFlow()
    
    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        _screenMutableState.value = FriendsScreenState.Error(ErrorType.NoInternet)
    }
    
    private val accessToken = shPrefs.getString(ACCESS_TOKEN_KEY, "") ?: ""
    
    fun handleEvent(event: FriendsScreenEvent) {
        when (event) {
            is FriendsScreenEvent.Start -> onStart()
            is FriendsScreenEvent.Retry -> onStart()
        }
    }
    
    private fun onStart() {
        if (accessToken.isEmpty()) {
            _screenMutableState.value = FriendsScreenState.Error(ErrorType.NoAccessToken)
            return
        }
        
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            val friends = repository.getFriends(accessToken)
            _screenMutableState.value = FriendsScreenState.Success(friends)
        }
    }
    
    companion object {
        private const val ACCESS_TOKEN_KEY = "accessToken"
    }
    
}
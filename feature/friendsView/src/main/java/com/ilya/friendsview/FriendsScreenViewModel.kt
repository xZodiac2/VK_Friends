package com.ilya.friendsview

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ilya.data.VkRepository
import com.ilya.friendsview.screen.FiendsScreenEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class FriendsScreenViewModel @Inject constructor(
    private val repository: VkRepository,
    @Named("accessToken")
    private val accessToken: String,
    @ApplicationContext context: Context,
) : ViewModel() {
    
    private val _screenMutableState = MutableStateFlow(FriendsScreenState.Loading)
    val screenState = _screenMutableState.asStateFlow()
    
    fun handleEvent(event: FiendsScreenEvent) {
        when (event) {
            FiendsScreenEvent.Start -> onStart()
            FiendsScreenEvent.Restart -> onRestart()
        }
    }
    
    private fun onStart() {
        viewModelScope.launch(Dispatchers.IO) {
            if (accessToken.isEmpty()) {
            
            }
        }
    }
    
    private fun onRestart() {
    
    }
    
    
}
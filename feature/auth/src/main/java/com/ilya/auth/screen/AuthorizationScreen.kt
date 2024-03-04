package com.ilya.auth.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ilya.auth.AuthorizationScreenViewModel
import com.vk.id.onetap.compose.onetap.OneTap


@Composable
fun AuthorizationScreen(
    onAuthorize: () -> Unit,
    authViewModel: AuthorizationScreenViewModel = hiltViewModel(),
) {
    val screenState by authViewModel.authorizationScreenState.collectAsState()
    val context = LocalContext.current
    
    Log.d("mytag", screenState.toString())
    
    when (screenState) {
        AuthorizationScreenState.NotAuthorized -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                OneTap(onAuth = {
                    authViewModel.handleEvent(AuthorizationScreenEvent.Authorize(it))
                }, onFail = {
                    Toast.makeText(context, it.description, Toast.LENGTH_LONG).show()
                }, modifier = Modifier.padding(horizontal = 32.dp))
            }
        }
        
        AuthorizationScreenState.Authorized -> onAuthorize()
        AuthorizationScreenState.Idle -> Unit
    }
    
    LaunchedEffect(key1 = Unit, block = {
        authViewModel.handleEvent(AuthorizationScreenEvent.Start)
    })
}


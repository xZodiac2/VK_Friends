package com.ilya.auth.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ilya.auth.AuthorizationScreenViewModel
import com.ilya.core.appCommon.compose.basicComposables.snackbar.SnackbarEventEffect
import com.ilya.theme.LocalColorScheme
import com.vk.id.onetap.compose.onetap.OneTap


@Composable
fun AuthorizationScreen(onAuthorized: () -> Unit) {
    val viewModel = hiltViewModel<AuthorizationScreenViewModel>()

    val screenState by viewModel.authorizationScreenState.collectAsState()
    val snackbarState by viewModel.snackbarState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    SnackbarEventEffect(
        state = snackbarState,
        onConsumed = { viewModel.handleEvent(AuthorizationScreenEvent.SnackbarConsumed) },
        action = { snackbarHostState.showSnackbar(it) }
    )

    when (screenState) {
        AuthorizationScreenState.NotAuthorized -> {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                snackbarHost = { SnackbarHost(snackbarHostState) },
                containerColor = LocalColorScheme.current.background
            ) { padding ->
                Box(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    OneTap(
                        modifier = Modifier.padding(horizontal = 32.dp),
                        onAuth = { viewModel.handleEvent(AuthorizationScreenEvent.Authorize(it)) },
                        onFail = { viewModel.handleEvent(AuthorizationScreenEvent.Fail) }
                    )
                }
            }
        }

        AuthorizationScreenState.Authorized -> onAuthorized()
    }

}


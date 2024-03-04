package com.ilya.friendsview.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ilya.core.basicComposables.BaseButton
import com.ilya.data.User
import com.ilya.friendsview.ErrorType
import com.ilya.friendsview.FriendsScreenState
import com.ilya.friendsview.FriendsScreenViewModel
import com.ilya.friendsview.R
import com.ilya.theme.LocalColorScheme


@Composable
fun FriendsScreen(
    onAgree: () -> Unit,
    viewModel: FriendsScreenViewModel = hiltViewModel(),
) {
    val state = viewModel.screenState.collectAsState()
    
    when (val stateValue = state.value) {
        is FriendsScreenState.Loading -> OnLoadingState()
        is FriendsScreenState.Error -> OnErrorState(
            error = stateValue.error,
            onAgree = onAgree,
            onRetry = {
                viewModel.handleEvent(FriendsScreenEvent.Retry)
            }
        )
        
        is FriendsScreenState.Success -> OnSuccessState(stateValue.friends)
    }
    
    
    LaunchedEffect(key1 = Unit, block = {
        viewModel.handleEvent(FriendsScreenEvent.Start)
    })
}

@Composable
private fun OnSuccessState(state: List<User>) {
    Text(text = "Success")
}


@Composable
private fun OnLoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}


@Composable
private fun OnErrorState(error: ErrorType, onAgree: () -> Unit, onRetry: () -> Unit) {
    when (error) {
        is ErrorType.NoAccessToken -> NoAccessToken(onAgree)
        is ErrorType.NoInternet -> NoInternet(onRetry)
    }
}


@Composable
private fun NoInternet(onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.error_no_internet),
            textAlign = TextAlign.Center,
            color = LocalColorScheme.current.primaryTextColor
        )
        BaseButton(onClick = onRetry) {
            Text(text = stringResource(id = R.string.retry))
        }
    }
}


@Composable
private fun NoAccessToken(onAgree: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.error_no_access_token),
            textAlign = TextAlign.Center,
            color = LocalColorScheme.current.primaryTextColor
        )
        BaseButton(
            onClick = onAgree,
        ) {
            Text(text = stringResource(id = R.string.ok))
        }
    }
}
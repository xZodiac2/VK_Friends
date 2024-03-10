package com.ilya.friendsview.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.ilya.friendsview.screen.friendsList.FriendsList
import com.ilya.theme.LocalColorScheme


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FriendsScreen(
    onEmptyAccessToken: () -> Unit,
    viewModel: FriendsScreenViewModel = hiltViewModel(),
    onProfileViewButtonClick: (Long) -> Unit,
) {
    val state = viewModel.screenState.collectAsState()
    val isRefreshing by viewModel.pullRefreshState.collectAsState()
    
    val pullRefreshState = rememberPullRefreshState(refreshing = isRefreshing, onRefresh = {
        viewModel.handleEvent(FriendsScreenEvent.Refresh)
    })
    
    when (val stateValue = state.value) {
        is FriendsScreenState.Loading -> OnLoadingState()
        is FriendsScreenState.Error -> OnErrorState(
            error = stateValue.error,
            onEmptyAccessToken = onEmptyAccessToken,
            onRetry = {
                viewModel.handleEvent(FriendsScreenEvent.Retry)
            }
        )
        
        is FriendsScreenState.Success -> OnSuccessState(
            state = stateValue.friends,
            pullRefreshState = pullRefreshState,
            refreshing = isRefreshing,
            onProfileViewButtonClick = onProfileViewButtonClick
        )
    }
    
    
    LaunchedEffect(key1 = Unit, block = {
        viewModel.handleEvent(FriendsScreenEvent.Start)
    })
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun OnSuccessState(
    state: List<User>,
    pullRefreshState: PullRefreshState,
    refreshing: Boolean,
    onProfileViewButtonClick: (Long) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        FriendsList(users = state, onProfileViewButtonClick = onProfileViewButtonClick)
        PullRefreshIndicator(
            refreshing = refreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}


@Composable
private fun OnLoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}


@Composable
private fun OnErrorState(error: ErrorType, onEmptyAccessToken: () -> Unit, onRetry: () -> Unit) {
    when (error) {
        is ErrorType.NoAccessToken -> onEmptyAccessToken()
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



package com.ilya.profileview.presentation.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.ilya.core.appCommon.StringResource
import com.ilya.core.basicComposables.OnError
import com.ilya.core.basicComposables.snackbar.SnackbarEventEffect
import com.ilya.profileViewDomain.User
import com.ilya.profileview.R
import com.ilya.profileview.presentation.ProfileScreenViewModel
import com.ilya.profileview.presentation.screen.components.ProfileHeader
import com.ilya.theme.LocalColorScheme

@Composable
fun ProfileScreen(
    userId: Long,
    onEmptyAccessToken: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: ProfileScreenViewModel = hiltViewModel()
) {
    val screenState = viewModel.screenStateFlow.collectAsState()
    val snackbarState by viewModel.snackbarState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val postsPagingItem = viewModel.pagingFlow.collectAsLazyPagingItems()

    Log.d("mytag", postsPagingItem.itemSnapshotList.items.toString())

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = LocalColorScheme.current.primary
    ) { paddingValues ->
        when (val state = screenState.value) {
            ProfileScreenState.Loading -> OnLoadingState(paddingValues)
            is ProfileScreenState.Error -> OnErrorState(
                errorType = state.errorType,
                onEmptyAccessToken = onEmptyAccessToken,
                tryAgainClick = { viewModel.handleEvent(ProfileScreenEvent.Retry) },
                paddingValues = paddingValues
            )

            is ProfileScreenState.Success -> Content(
                user = state.user,
                onBackClick = onBackClick,
                friendRequest = { viewModel.handleEvent(ProfileScreenEvent.FriendRequest(it)) },
                paddingValues = paddingValues
            )
        }
    }

    SnackbarEventEffect(
        state = snackbarState,
        onConsumed = { viewModel.handleEvent(ProfileScreenEvent.SnackbarConsumed) },
        action = { snackbarHostState.showSnackbar(it) }
    )

    LaunchedEffect(key1 = Unit) {
        viewModel.handleEvent(ProfileScreenEvent.Start(userId))
    }

}

@Composable
private fun OnErrorState(
    errorType: ErrorType,
    onEmptyAccessToken: () -> Unit,
    tryAgainClick: () -> Unit,
    paddingValues: PaddingValues
) {
    Box(modifier = Modifier.padding(paddingValues)) {
        when (errorType) {
            ErrorType.NoInternet -> OnError(
                modifier = Modifier.fillMaxHeight(),
                message = StringResource.Resource(id = R.string.no_able_to_get_data),
                buttonText = StringResource.Resource(id = R.string.try_again),
                onButtonClick = tryAgainClick
            )

            ErrorType.NoAccessToken -> onEmptyAccessToken()
            is ErrorType.Unknown -> OnError(
                modifier = Modifier.fillMaxHeight(),
                message = StringResource.Resource(
                    id = R.string.unknown_error,
                    listOf(errorType.error.toString())
                ),
                buttonText = StringResource.Resource(id = R.string.try_again),
                onButtonClick = tryAgainClick
            )
        }
    }
}

@Composable
private fun OnLoadingState(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) { CircularProgressIndicator(color = LocalColorScheme.current.primaryIconTintColor) }
}

@Composable
private fun Content(
    user: User,
    onBackClick: () -> Unit,
    friendRequest: (User) -> Unit,
    paddingValues: PaddingValues
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { ProfileHeader(user, onBackClick, friendRequest) }
    }
}

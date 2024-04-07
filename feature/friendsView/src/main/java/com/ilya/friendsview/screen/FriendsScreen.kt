package com.ilya.friendsview.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.ilya.core.appCommon.StringResource
import com.ilya.core.basicComposables.OnError
import com.ilya.core.basicComposables.alertDialog.AlertDialogStateHandler
import com.ilya.core.basicComposables.snackbar.SnackbarEventEffect
import com.ilya.data.paging.PaginationError
import com.ilya.data.paging.User
import com.ilya.friendsview.FriendsScreenViewModel
import com.ilya.friendsview.R
import com.ilya.friendsview.screen.friendsList.FriendsList
import com.ilya.theme.LocalColorScheme
import com.ilya.theme.LocalTypography


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreen(
    onEmptyAccessToken: () -> Unit,
    onProfileViewButtonClick: (Long) -> Unit,
    onExitConfirm: () -> Unit,
    viewModel: FriendsScreenViewModel = hiltViewModel(),
) {
    val pagingState = viewModel.pagingFlow.collectAsLazyPagingItems()
    val alertDialogState = viewModel.alertDialogState.collectAsState()
    val snackbarState by viewModel.snackbarState.collectAsState()
    val accountOwner by viewModel.accountOwnerState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    BackHandler { viewModel.handleEvent(FriendsScreenEvent.BackPress(onExitConfirm)) }
    AlertDialogStateHandler(state = alertDialogState.value)

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopBar(
                accountOwner = accountOwner,
                onProfileViewButtonClick = onProfileViewButtonClick,
                onPlaceholderAvatarClick = { viewModel.handleEvent(FriendsScreenEvent.PlaceholderAvatarClick) },
                scrollBehavior = scrollBehavior
            )
        },
        containerColor = LocalColorScheme.current.primary
    ) { paddingValues ->
        Content(
            pagingState = pagingState,
            paddingValues = paddingValues,
            onEmptyAccessToken = onEmptyAccessToken,
            onProfileViewButtonClick = onProfileViewButtonClick,
        )
    }

    SnackbarEventEffect(
        state = snackbarState,
        onConsumed = { viewModel.handleEvent(FriendsScreenEvent.SnackbarConsumed) },
        action = { snackbarHostState.showSnackbar(it) }
    )

    LaunchedEffect(key1 = Unit) {
        viewModel.handleEvent(FriendsScreenEvent.Start)
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    accountOwner: User?,
    onProfileViewButtonClick: (Long) -> Unit,
    onPlaceholderAvatarClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.screen_name),
                color = LocalColorScheme.current.primaryTextColor,
                fontSize = LocalTypography.current.large,
                fontWeight = FontWeight.W500,
                modifier = Modifier.padding(start = 12.dp)
            )
        },
        navigationIcon = {
            when (accountOwner) {
                null -> {
                    Image(
                        painter = painterResource(id = R.drawable.avatar),
                        contentDescription = "avatarPlaceholder",
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onPlaceholderAvatarClick),
                        contentScale = ContentScale.Crop
                    )
                }

                else -> {
                    AsyncImage(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .clickable { onProfileViewButtonClick(accountOwner.id) },
                        model = accountOwner.photoUrl,
                        contentDescription = "ownerPhoto200",
                        contentScale = ContentScale.Crop
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = LocalColorScheme.current.secondary,
            scrolledContainerColor = LocalColorScheme.current.secondary
        ),
        windowInsets = WindowInsets(left = 16.dp),
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun Content(
    pagingState: LazyPagingItems<User>,
    paddingValues: PaddingValues,
    onEmptyAccessToken: () -> Unit,
    onProfileViewButtonClick: (Long) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        when (val refreshState = pagingState.loadState.refresh) {
            is LoadState.Loading -> OnLoadingState()
            is LoadState.Error -> {
                /**
                 *  If error is [PaginationError.NoInternet], [LazyPagingItems] will be receive
                 *  from local database
                 */
                if (refreshState.error == PaginationError.NoInternet) {
                    OnSuccessState(
                        pagingState = pagingState,
                        onProfileViewButtonClick = onProfileViewButtonClick,
                        onEmptyAccessToken = onEmptyAccessToken
                    )
                }
                OnErrorState(
                    error = when (refreshState.error) {
                        is PaginationError.NoAccessToken -> ErrorType.NoAccessToken
                        is PaginationError.NoInternet -> ErrorType.NoInternet
                        else -> ErrorType.Unknown(refreshState.error)
                    },
                    onEmptyAccessToken = onEmptyAccessToken,
                    onRetry = { pagingState.retry() }
                )
            }

            is LoadState.NotLoading -> OnSuccessState(
                pagingState = pagingState,
                onProfileViewButtonClick = onProfileViewButtonClick,
                onEmptyAccessToken = onEmptyAccessToken
            )
        }
    }
}

@Composable
private fun OnSuccessState(
    pagingState: LazyPagingItems<User>,
    onProfileViewButtonClick: (Long) -> Unit,
    onEmptyAccessToken: () -> Unit
) {
    FriendsList(
        pagingState = pagingState,
        onProfileViewButtonClick = onProfileViewButtonClick,
        onEmptyAccessToken = onEmptyAccessToken,
    )
}


@Composable
private fun OnLoadingState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalColorScheme.current.primary),
        contentAlignment = Alignment.Center
    ) { CircularProgressIndicator() }
}


@Composable
private fun OnErrorState(
    error: ErrorType,
    onEmptyAccessToken: () -> Unit,
    onRetry: () -> Unit,
) {
    when (error) {
        ErrorType.NoAccessToken -> onEmptyAccessToken()
        is ErrorType.Unknown -> OnError(
            modifier = Modifier.fillMaxHeight(),
            message = StringResource.Resource(
                id = R.string.unknown_error,
                arguments = listOf(error.error.message ?: "")
            ),
            buttonText = StringResource.Resource(id = R.string.retry),
            onTryAgainClick = onRetry
        )

        ErrorType.NoInternet -> Unit
    }
}


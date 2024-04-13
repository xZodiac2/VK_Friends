package com.example.search.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import com.example.search.R
import com.example.search.SearchViewModel
import com.example.search.screen.elements.SearchBar
import com.example.search.screen.elements.usersList
import com.ilya.core.appCommon.StringResource
import com.ilya.core.basicComposables.OnError
import com.ilya.core.basicComposables.snackbar.SnackbarEventEffect
import com.ilya.data.paging.PaginationError
import com.ilya.data.paging.User
import com.ilya.theme.LocalColorScheme
import com.ilya.theme.LocalTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    openProfileRequest: (Long) -> Unit,
    onEmptyAccessToken: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val pagingItems = viewModel.pagingFlow.collectAsLazyPagingItems()
    val accountOwner by viewModel.accountOwnerStateFlow.collectAsState()
    val snackbarState by viewModel.snackbarStateFlow.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Column {
                TopBar(
                    accountOwner = accountOwner,
                    onAvatarClick = openProfileRequest,
                    onPlaceholderAvatarClick = { viewModel.handleEvent(SearchScreenEvent.PlugAvatarClick) },
                    scrollBehavior = scrollBehavior
                )
                SearchBar(
                    onSearch = { viewModel.handleEvent(SearchScreenEvent.Search(it)) },
                    heightOffset = scrollBehavior.state.heightOffset,
                    heightOffsetLimit = scrollBehavior.state.heightOffsetLimit
                )
            }
        },
        containerColor = LocalColorScheme.current.primary
    ) { padding ->
        Content(
            pagingItems = pagingItems,
            onTryAgainClick = { pagingItems.refresh() },
            onEmptyAccessToken = onEmptyAccessToken,
            onCardClick = openProfileRequest,
            paddingValues = padding
        )
    }

    SnackbarEventEffect(
        state = snackbarState,
        onConsumed = { viewModel.handleEvent(SearchScreenEvent.SnackbarConsumed) },
        action = { snackbarHostState.showSnackbar(it) }
    )

    LaunchedEffect(key1 = Unit) {
        viewModel.handleEvent(SearchScreenEvent.Start)
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    accountOwner: User?,
    onAvatarClick: (Long) -> Unit,
    onPlaceholderAvatarClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.search_screen_name),
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
                            .clickable { onAvatarClick(accountOwner.id) },
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
    pagingItems: LazyPagingItems<User>,
    onTryAgainClick: () -> Unit,
    onEmptyAccessToken: () -> Unit,
    onCardClick: (Long) -> Unit,
    paddingValues: PaddingValues
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(paddingValues)
    ) {
        when (val refreshLoadState = pagingItems.loadState.refresh) {
            LoadState.Loading -> item(span = { GridItemSpan(2) }) { OnLoadingState() }
            is LoadState.Error -> {
                /**
                 * If refreshLoadState is [LoadState.NotLoading], [LazyPagingItems] will be
                 * receive from local database
                 */
                if (refreshLoadState.error == PaginationError.NoInternet) {
                    usersList(
                        pagingItems = pagingItems,
                        onCardClick = onCardClick,
                        onEmptyAccessToken = onEmptyAccessToken,
                        onTryAgainClick = { pagingItems.retry() }
                    )
                } else {
                    item {
                        OnErrorState(
                            errorType = when (refreshLoadState.error) {
                                is PaginationError.NoInternet -> ErrorType.NoInternet
                                is PaginationError.NoAccessToken -> ErrorType.NoAccessToken
                                else -> ErrorType.Unknown(refreshLoadState.error)
                            },
                            onTryAgainClick = onTryAgainClick,
                            onEmptyAccessToken = onEmptyAccessToken
                        )
                    }
                }
            }

            is LoadState.NotLoading -> usersList(
                pagingItems = pagingItems,
                onCardClick = onCardClick,
                onEmptyAccessToken = onEmptyAccessToken,
                onTryAgainClick = { pagingItems.retry() }
            )
        }
    }
}

@Composable
private fun OnErrorState(
    errorType: ErrorType,
    onTryAgainClick: () -> Unit,
    onEmptyAccessToken: () -> Unit,
) {
    when (errorType) {
        ErrorType.NoInternet -> Unit
        ErrorType.NoAccessToken -> onEmptyAccessToken()
        is ErrorType.Unknown -> OnError(
            modifier = Modifier.fillMaxHeight(),
            message = StringResource.Resource(
                id = R.string.error_unknown,
                arguments = listOf(errorType.error.message ?: "")
            ),
            buttonText = StringResource.Resource(id = R.string.retry),
            onTryAgainClick = onTryAgainClick
        )
    }
}


@Composable
private fun OnLoadingState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .height(400.dp)
            .background(LocalColorScheme.current.primary),
        contentAlignment = Alignment.Center
    ) { CircularProgressIndicator() }
}

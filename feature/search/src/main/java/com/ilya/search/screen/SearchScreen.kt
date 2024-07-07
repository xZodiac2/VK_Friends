package com.ilya.search.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ilya.core.basicComposables.snackbar.SnackbarEventEffect
import com.ilya.data.paging.User
import com.ilya.search.R
import com.ilya.search.SearchViewModel
import com.ilya.search.screen.components.OnEmptyUsers
import com.ilya.search.screen.components.ResolveAppend
import com.ilya.search.screen.components.ResolveRefresh
import com.ilya.search.screen.components.SearchBar
import com.ilya.search.screen.components.UserCard
import com.ilya.theme.LocalColorScheme
import com.ilya.theme.LocalTypography

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun SearchScreen(
    openProfileRequest: (userId: Long, isPrivate: Boolean) -> Unit,
    onEmptyAccessToken: () -> Unit
) {
    val viewModel: SearchViewModel = hiltViewModel()

    val users = viewModel.pagingFlow.collectAsLazyPagingItems()
    val accountOwner by viewModel.accountOwnerStateFlow.collectAsState()
    val snackbarState by viewModel.snackbarStateFlow.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var initialDataLoaded by remember { mutableStateOf(false) }

    val isRefreshing = users.loadState.refresh == LoadState.Loading && initialDataLoaded

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { users.refresh() }
    )

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Column {
                TopBar(
                    accountOwner = accountOwner,
                    onAvatarClick = { openProfileRequest(it, false) },
                    onPlaceholderClick = { viewModel.handleEvent(SearchScreenEvent.PlugAvatarClick) },
                    scrollBehavior = scrollBehavior
                )
                SearchBar(
                    onSearch = {
                        initialDataLoaded = false
                        viewModel.handleEvent(SearchScreenEvent.Search(it))
                    },
                    heightOffset = scrollBehavior.state.heightOffset,
                    heightOffsetLimit = scrollBehavior.state.heightOffsetLimit
                )
            }
        },
        containerColor = LocalColorScheme.current.primary
    ) { padding ->
        Content(
            users = users,
            onEmptyAccessToken = onEmptyAccessToken,
            onCardClick = openProfileRequest,
            paddingValues = padding,
            isRefreshing = isRefreshing,
            pullRefreshState = pullRefreshState
        )
    }

    SnackbarEventEffect(
        state = snackbarState,
        onConsumed = { viewModel.handleEvent(SearchScreenEvent.SnackbarConsumed) },
        action = { snackbarHostState.showSnackbar(it) }
    )

    LaunchedEffect(Unit) {
        snapshotFlow { users.itemCount }.collect {
            if (it == 0) return@collect
            initialDataLoaded = true
        }
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.handleEvent(SearchScreenEvent.Start)
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    accountOwner: User?,
    onAvatarClick: (Long) -> Unit,
    onPlaceholderClick: () -> Unit,
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
            AsyncImage(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .clickable { accountOwner?.id?.let(onAvatarClick) ?: onPlaceholderClick() },
                model = ImageRequest.Builder(LocalContext.current)
                    .placeholder(R.drawable.avatar)
                    .fallback(R.drawable.avatar)
                    .data(accountOwner?.photoUrl)
                    .build(),
                contentDescription = "ownerPhoto200",
                contentScale = ContentScale.Crop
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = LocalColorScheme.current.secondary,
            scrolledContainerColor = LocalColorScheme.current.secondary
        ),
        windowInsets = WindowInsets(left = 16.dp),
        scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun Content(
    users: LazyPagingItems<User>,
    onEmptyAccessToken: () -> Unit,
    onCardClick: (userId: Long, isPrivate: Boolean) -> Unit,
    paddingValues: PaddingValues,
    isRefreshing: Boolean,
    pullRefreshState: PullRefreshState
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .pullRefresh(pullRefreshState)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(count = users.itemCount) { User(users, it, onCardClick) }
            item(span = { GridItemSpan(2) }) { ResolveRefresh(users, onEmptyAccessToken) }
            item(span = { GridItemSpan(2) }) { ResolveAppend(users, onEmptyAccessToken) }
            item(span = { GridItemSpan(2) }) { OnEmptyUsers(users) }
        }

        PullRefreshIndicator(
            modifier = Modifier.align(Alignment.TopCenter),
            refreshing = isRefreshing,
            state = pullRefreshState,
            contentColor = LocalColorScheme.current.primaryIconTintColor,
            backgroundColor = LocalColorScheme.current.cardContainerColor
        )
    }
}

@Composable
private fun User(
    users: LazyPagingItems<User>,
    index: Int,
    onCardClick: (userId: Long, isPrivate: Boolean) -> Unit,
) {
    val user = users[index]
    if (user != null) {
        UserCard(
            onCardClick = { onCardClick(user.id, user.isClosed) },
            user = user
        )
    }
}



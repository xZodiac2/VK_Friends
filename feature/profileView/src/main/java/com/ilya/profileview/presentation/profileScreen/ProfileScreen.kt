package com.ilya.profileview.presentation.profileScreen

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.ilya.core.appCommon.StringResource
import com.ilya.core.basicComposables.OnError
import com.ilya.core.basicComposables.snackbar.SnackbarEventEffect
import com.ilya.profileViewDomain.models.Post
import com.ilya.profileViewDomain.models.User
import com.ilya.profileview.R
import com.ilya.profileview.presentation.profileScreen.components.Photos
import com.ilya.profileview.presentation.profileScreen.components.PostCard
import com.ilya.profileview.presentation.profileScreen.components.ProfileHeader
import com.ilya.profileview.presentation.profileScreen.states.ProfileScreenState
import com.ilya.theme.LocalColorScheme
import com.ilya.theme.LocalTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userId: Long,
    onEmptyAccessToken: () -> Unit,
    onBackClick: () -> Unit,
    onPhotoClick: (id: Long, targetPhotoIndex: Int) -> Unit,
    onOpenPhotosClick: (Long) -> Unit,
    viewModel: ProfileScreenViewModel = hiltViewModel()
) {
    val screenState = viewModel.screenState.collectAsState()
    val snackbarState by viewModel.snackbarState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val postsPagingItems = viewModel.postsFlow.collectAsLazyPagingItems()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = LocalColorScheme.current.primary,
        topBar = {
            TopBar(
                onBackClick = { onBackClick() },
                userId = userId,
                contentOffset = scrollBehavior.state.contentOffset
            )
        }
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
                posts = postsPagingItems,
                friendRequest = { viewModel.handleEvent(ProfileScreenEvent.FriendRequest(it)) },
                paddingValues = paddingValues,
                onPhotoClick = onPhotoClick,
                onOpenPhotosClick = { onOpenPhotosClick(state.user.id) }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    onBackClick: () -> Unit,
    userId: Long,
    contentOffset: Float
) {
    val contentScrolled = contentOffset < -50f
    val animatedBackgroundColor = animateColorAsState(
        targetValue = if (contentScrolled) {
            LocalColorScheme.current.secondary
        } else {
            LocalColorScheme.current.cardContainerColor
        },
        label = "topBarBackground"
    )

    TopAppBar(
        title = {
            Text(
                text = stringResource(
                    id = R.string.profile_screen_name,
                    formatArgs = listOf("id$userId").toTypedArray(),
                ),
                color = LocalColorScheme.current.primaryTextColor
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "back",
                    tint = LocalColorScheme.current.iconTintColor
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = animatedBackgroundColor.value
        )
    )
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
                message = StringResource.FromId(id = R.string.no_able_to_get_data),
                buttonText = StringResource.FromId(id = R.string.try_again),
                onButtonClick = tryAgainClick
            )

            ErrorType.NoAccessToken -> onEmptyAccessToken()
            is ErrorType.Unknown -> OnError(
                modifier = Modifier.fillMaxHeight(),
                message = StringResource.FromId(
                    id = R.string.unknown_error,
                    listOf(errorType.error.toString())
                ),
                buttonText = StringResource.FromId(id = R.string.try_again),
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
    posts: LazyPagingItems<Post>,
    friendRequest: (User) -> Unit,
    paddingValues: PaddingValues,
    onPhotoClick: (userId: Long, targetPhotoIndex: Int) -> Unit,
    onOpenPhotosClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { ProfileHeader(user, friendRequest) }
        item {
            Photos(
                photos = user.photos,
                onPhotoClick = onPhotoClick,
                onOpenPhotosClick = onOpenPhotosClick
            )
        }
        items(count = posts.itemCount) { index ->
            val post = posts[index]
            post?.let {
                PostCard(it)
            }
        }
        item {
            AppendIndicator(
                loadState = posts.loadState.append,
                onTryAgainClick = { posts.retry() }
            )
        }
        item {
            RefreshIndicator(
                loadState = posts.loadState.refresh,
                onTryAgainClick = { posts.refresh() }
            )
        }
        item { OnEmptyPostsMessage(posts) }
    }
}

@Composable
private fun OnEmptyPostsMessage(posts: LazyPagingItems<Post>) {
    if (posts.itemCount == 0 && posts.loadState.refresh is LoadState.NotLoading) {
        Card(
            modifier = Modifier.aspectRatio(2f / 1),
            colors = CardDefaults.cardColors(
                containerColor = LocalColorScheme.current.cardContainerColor,
                contentColor = LocalColorScheme.current.secondaryTextColor
            )
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.8f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Icon(
                        painter = painterResource(R.drawable.empty),
                        modifier = Modifier.fillMaxSize(0.5f),
                        contentDescription = "noPosts",
                    )
                    Text(
                        text = stringResource(R.string.no_posts),
                        fontSize = LocalTypography.current.big
                    )
                }
            }
        }
    }
}

@Composable
private fun RefreshIndicator(
    loadState: LoadState,
    onTryAgainClick: () -> Unit
) {
    when (loadState) {
        LoadState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = LocalColorScheme.current.primaryIconTintColor)
            }
        }

        is LoadState.Error -> {
            OnError(
                modifier = Modifier.fillMaxSize(),
                message = StringResource.FromId(
                    R.string.error_unknown,
                    listOf(loadState.error.message.toString())
                ),
                buttonText = StringResource.FromId(R.string.try_again),
                onButtonClick = onTryAgainClick
            )
        }

        else -> Unit
    }
}

@Composable
private fun AppendIndicator(
    loadState: LoadState,
    onTryAgainClick: () -> Unit
) {
    when (loadState) {
        LoadState.Loading -> {
            Box(
                modifier = Modifier
                    .height(220.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = LocalColorScheme.current.primaryIconTintColor)
            }
        }

        is LoadState.Error -> {
            OnError(
                modifier = Modifier.height(220.dp),
                message = StringResource.FromId(
                    R.string.error_unknown,
                    listOf(loadState.error.message.toString())
                ),
                buttonText = StringResource.FromId(R.string.try_again),
                onButtonClick = onTryAgainClick
            )
        }

        else -> Unit
    }
}

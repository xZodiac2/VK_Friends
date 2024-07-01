package com.ilya.profileview.presentation.profileScreen

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.ilya.core.appCommon.StringResource
import com.ilya.core.basicComposables.OnError
import com.ilya.core.basicComposables.snackbar.SnackbarEventEffect
import com.ilya.profileViewDomain.models.Post
import com.ilya.profileViewDomain.models.User
import com.ilya.profileview.R
import com.ilya.profileview.presentation.profileScreen.components.OnEmptyPostsMessage
import com.ilya.profileview.presentation.profileScreen.components.Photos
import com.ilya.profileview.presentation.profileScreen.components.PostCard
import com.ilya.profileview.presentation.profileScreen.components.ProfileHeader
import com.ilya.profileview.presentation.profileScreen.components.ResolveAppend
import com.ilya.profileview.presentation.profileScreen.components.ResolveRefresh
import com.ilya.theme.LocalColorScheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userId: Long,
    onEmptyAccessToken: () -> Unit,
    onBackClick: () -> Unit,
    onPhotoClick: (id: Long, targetPhotoIndex: Int) -> Unit,
    onOpenPhotosClick: (Long) -> Unit,
) {
    val viewModel: ProfileScreenViewModel = hiltViewModel()

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
        item { Photos(user.photos, onPhotoClick, onOpenPhotosClick) }
        items(count = posts.itemCount) { Post(posts, it) }
        item { ResolveAppend(posts) }
        item { ResolveRefresh(posts) }
        item { OnEmptyPostsMessage(posts) }
    }
}

@Composable
private fun Post(posts: LazyPagingItems<Post>, index: Int) {
    val post = posts[index]
    post?.let {
        PostCard(it)
    }
}
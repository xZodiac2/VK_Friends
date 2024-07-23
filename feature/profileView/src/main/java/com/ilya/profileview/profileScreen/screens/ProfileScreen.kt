package com.ilya.profileview.profileScreen.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.ilya.core.appCommon.StringResource
import com.ilya.core.basicComposables.OnError
import com.ilya.core.basicComposables.snackbar.SnackbarEventEffect
import com.ilya.paging.Audio
import com.ilya.paging.Post
import com.ilya.profileViewDomain.User
import com.ilya.profileview.R
import com.ilya.profileview.photosScreen.OnLoading
import com.ilya.profileview.profileScreen.AudioLoadIndicatorState
import com.ilya.profileview.profileScreen.ErrorType
import com.ilya.profileview.profileScreen.PostsLikesState
import com.ilya.profileview.profileScreen.ProfileScreenState
import com.ilya.profileview.profileScreen.ProfileScreenViewModel
import com.ilya.profileview.profileScreen.components.posts.OnEmptyPostsMessage
import com.ilya.profileview.profileScreen.components.posts.PostCard
import com.ilya.profileview.profileScreen.components.posts.ResolveAppend
import com.ilya.profileview.profileScreen.components.posts.ResolveRefresh
import com.ilya.profileview.profileScreen.components.profileCommon.Photos
import com.ilya.profileview.profileScreen.components.profileCommon.TopBar
import com.ilya.profileview.profileScreen.components.profileCommon.profileHeader.ProfileHeader
import com.ilya.profileview.profileScreen.screens.event.EventReceiver
import com.ilya.profileview.profileScreen.screens.event.ProfileScreenNavEvent
import com.ilya.theme.LocalColorScheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userId: Long,
    isPrivate: Boolean,
    handleNavEvent: (ProfileScreenNavEvent) -> Unit
) {
    val viewModel = hiltViewModel<ProfileScreenViewModel>()
    val eventReceiver = EventReceiver(viewModel)

    if (isPrivate) {
        PrivateProfile(viewModel, userId, handleNavEvent)
        return
    }

    val screenState = viewModel.screenState.collectAsState()
    val likesState by viewModel.likesState.collectAsState()
    val currentLoopingAudio by viewModel.currentLoopingAudio.collectAsState()
    val snackbarState by viewModel.snackbarState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val posts = viewModel.postsFlow.collectAsLazyPagingItems()
    val audioLoadingState by viewModel.audioIndicatorState.collectAsState()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    BackHandler(onBack = eventReceiver::onBackClick)

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = LocalColorScheme.current.primary,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            /**
             * Using `eventReceiver::onBackClick` without `remember` causes many unnecessary recompositions of `IconButton` inside the `TopBar`
             */
            val onBackClick = remember { eventReceiver::onBackClick }

            TopBar(
                userId = userId,
                contentOffset = scrollBehavior.state.contentOffset,
                onBackClick = onBackClick
            )
        }
    ) { padding ->
        when (val state = screenState.value) {
            ProfileScreenState.Loading -> OnLoading(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxHeight()
            )

            is ProfileScreenState.Error -> OnErrorState(
                errorType = state.errorType,
                onEmptyAccessToken = {
                    eventReceiver.onEmptyAccessToken()
                },
                onTryAgainClick = { eventReceiver.onRetry() },
                padding = padding
            )

            is ProfileScreenState.ViewData -> {
                Box {
                    Content(
                        user = state.user,
                        posts = posts,
                        paddingValues = padding,
                        likes = likesState,
                        currentLoopingAudio = currentLoopingAudio,
                        eventReceiver = eventReceiver
                    )
                    if (audioLoadingState == AudioLoadIndicatorState.Loading) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.dp)
                                .align(Alignment.BottomCenter),
                            color = LocalColorScheme.current.primaryIconTintColor,
                            trackColor = LocalColorScheme.current.primary
                        )
                    }
                }

                LaunchedEffect(Unit) {
                    snapshotFlow { posts.itemSnapshotList.items }.collect { posts ->
                        val newLikes = posts.associate { it.id to it.likes }.filterNot {
                            it.key in likesState.likes.keys
                        }
                        eventReceiver.onPostsAdded(newLikes)
                    }
                }
            }
        }
    }

    SnackbarEventEffect(
        state = snackbarState,
        onConsumed = eventReceiver::onSnackbarConsumed,
        action = { snackbarHostState.showSnackbar(it) }
    )

    LaunchedEffect(Unit) {
        eventReceiver.onStart(userId)
        viewModel.navEventFlow.collect {
            handleNavEvent(it)
        }
    }

}

@Composable
internal fun OnErrorState(
    errorType: ErrorType,
    onEmptyAccessToken: () -> Unit,
    onTryAgainClick: () -> Unit,
    padding: PaddingValues
) {
    Box(modifier = Modifier.padding(padding)) {
        when (errorType) {
            ErrorType.NoInternet -> OnError(
                modifier = Modifier.fillMaxHeight(),
                message = StringResource.FromId(id = R.string.no_able_to_get_data),
                buttonText = StringResource.FromId(id = R.string.try_again),
                onButtonClick = onTryAgainClick
            )

            ErrorType.NoAccessToken -> onEmptyAccessToken()
            is ErrorType.Unknown -> OnError(
                modifier = Modifier.fillMaxHeight(),
                message = StringResource.FromId(
                    id = R.string.error_unknown,
                    listOf(errorType.error.toString())
                ),
                buttonText = StringResource.FromId(id = R.string.try_again),
                onButtonClick = onTryAgainClick
            )
        }
    }
}

@Composable
private fun Content(
    user: User,
    posts: LazyPagingItems<Post>,
    paddingValues: PaddingValues,
    likes: PostsLikesState,
    currentLoopingAudio: Pair<Audio?, Boolean>,
    eventReceiver: EventReceiver
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { ProfileHeader(user, eventReceiver) }
        item {
            Photos(
                photos = user.photos,
                onPhotoClick = { userId, targetPhotoIndex ->
                    eventReceiver.onPhotoClick(userId, targetPhotoIndex)
                },
                onOpenPhotosClick = { eventReceiver.onOpenPhotosClick(user.id) }
            )
        }
        items(count = posts.itemCount, key = posts.itemKey { it.id }) {
            Post(
                posts = posts,
                index = it,
                currentLoopingAudio = currentLoopingAudio,
                likesState = likes,
                eventReceiver = eventReceiver
            )
        }
        item { ResolveAppend(posts, eventReceiver) }
        item { ResolveRefresh(posts, eventReceiver) }
        item { OnEmptyPostsMessage(posts) }
    }
}

@Composable
private fun Post(
    posts: LazyPagingItems<Post>,
    index: Int,
    currentLoopingAudio: Pair<Audio?, Boolean>,
    likesState: PostsLikesState,
    eventReceiver: EventReceiver
) {
    val post = posts[index]

    post?.let {
        PostCard(
            post = it,
            likes = likesState.likes[post.id],
            currentLoopingAudio = currentLoopingAudio,
            eventReceiver = eventReceiver,
        )
    }
}
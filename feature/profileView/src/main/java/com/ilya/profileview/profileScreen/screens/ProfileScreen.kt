package com.ilya.profileview.profileScreen.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.ilya.core.appCommon.StringResource
import com.ilya.core.basicComposables.OnError
import com.ilya.core.basicComposables.snackbar.SnackbarEventEffect
import com.ilya.paging.models.Audio
import com.ilya.paging.models.Post
import com.ilya.profileViewDomain.User
import com.ilya.profileview.R
import com.ilya.profileview.photosScreen.OnLoading
import com.ilya.profileview.profileScreen.AudioLoadIndicatorState
import com.ilya.profileview.profileScreen.ErrorType
import com.ilya.profileview.profileScreen.PostsLikesState
import com.ilya.profileview.profileScreen.ProfileScreenState
import com.ilya.profileview.profileScreen.ProfileScreenViewModel
import com.ilya.profileview.profileScreen.components.posts.CommentsBottomSheet
import com.ilya.profileview.profileScreen.components.posts.PostCard
import com.ilya.profileview.profileScreen.components.profileCommon.Photos
import com.ilya.profileview.profileScreen.components.profileCommon.TopBar
import com.ilya.profileview.profileScreen.components.profileCommon.profileHeader.ProfileHeader
import com.ilya.profileview.profileScreen.components.profileCommon.shared.OnEmptyPostsMessage
import com.ilya.profileview.profileScreen.components.profileCommon.shared.ResolveAppend
import com.ilya.profileview.profileScreen.components.profileCommon.shared.ResolveRefresh
import com.ilya.profileview.profileScreen.screens.event.EventReceiver
import com.ilya.profileview.profileScreen.screens.event.ProfileScreenNavEvent
import com.ilya.theme.LocalColorScheme
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userId: Long,
    isPrivate: Boolean,
    handleNavEvent: (ProfileScreenNavEvent) -> Unit
) {
    val viewModel = hiltViewModel<ProfileScreenViewModel>()
    val eventReceiver = remember { EventReceiver(viewModel) }

    if (isPrivate) {
        PrivateProfile(viewModel, userId, handleNavEvent)
        return
    }

    val screenState = viewModel.screenState.collectAsState()
    val likesState = viewModel.likesState.collectAsState()
    val currentLoopingAudio = viewModel.currentLoopingAudio.collectAsState()
    val snackbarState = viewModel.snackbarState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val posts = remember { viewModel.postsFlow }
    val audioLoadingState = viewModel.audioIndicatorState.collectAsState()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    BackHandler(onBack = eventReceiver::onBackClick)

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = LocalColorScheme.current.primary,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            val contentScrolled by remember { derivedStateOf { scrollBehavior.state.contentOffset < -50 } }
            val onBackClick = remember { eventReceiver::onBackClick }

            TopBar(
                userId = userId,
                contentScrolled = contentScrolled,
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
                        postsFlow = posts,
                        paddingValues = padding,
                        likes = likesState,
                        currentLoopingAudio = currentLoopingAudio,
                        eventReceiver = eventReceiver
                    )
                    AudioLoadIndicator(audioLoadingState)
                }
            }
        }
    }

    CommentsBottomSheet(viewModel.bottomSheetState, eventReceiver)

    SnackbarEventEffect(
        state = snackbarState.value,
        onConsumed = eventReceiver::onSnackbarConsumed,
        action = { snackbarHostState.showSnackbar(it) }
    )

    LaunchedEffect(Unit) {
        eventReceiver.onStart(userId)
        viewModel.navEventFlow.collect(handleNavEvent)
    }

}

@Composable
private fun BoxScope.AudioLoadIndicator(audioLoadingState: State<AudioLoadIndicatorState>) {
    if (audioLoadingState.value == AudioLoadIndicatorState.Loading) {
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
    postsFlow: Flow<PagingData<Post>>,
    paddingValues: PaddingValues,
    likes: State<PostsLikesState>,
    currentLoopingAudio: State<Pair<Audio?, Boolean>>,
    eventReceiver: EventReceiver
) {
    val posts = postsFlow.collectAsLazyPagingItems()

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
        items(count = posts.itemCount, key = posts.itemKey { posts.itemSnapshotList.indexOf(it) }) {
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

    LaunchedEffect(Unit) {
        snapshotFlow { posts.itemSnapshotList.items }.collect { posts ->
            val newLikes = posts.associate { it.id to it.likes }.filterNot {
                it.key in likes.value.likes.keys
            }
            eventReceiver.onPostsAdded(newLikes)
        }
    }

}

@Composable
private fun Post(
    posts: LazyPagingItems<Post>,
    index: Int,
    currentLoopingAudio: State<Pair<Audio?, Boolean>>,
    likesState: State<PostsLikesState>,
    eventReceiver: EventReceiver
) {
    val post = remember { posts[index] }

    post?.let {
        PostCard(
            post = it,
            likes = likesState,
            currentLoopingAudio = currentLoopingAudio,
            eventReceiver = eventReceiver,
        )
    }
}
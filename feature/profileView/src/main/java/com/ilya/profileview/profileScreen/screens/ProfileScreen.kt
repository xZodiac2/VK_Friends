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
import com.ilya.core.appCommon.StringResource
import com.ilya.core.basicComposables.OnError
import com.ilya.core.basicComposables.snackbar.SnackbarEventEffect
import com.ilya.profileViewDomain.models.Audio
import com.ilya.profileViewDomain.models.Post
import com.ilya.profileViewDomain.models.User
import com.ilya.profileViewDomain.models.Video
import com.ilya.profileview.R
import com.ilya.profileview.photosScreen.OnLoading
import com.ilya.profileview.profileScreen.AudioLoadIndicatorState
import com.ilya.profileview.profileScreen.ErrorType
import com.ilya.profileview.profileScreen.PostsLikesState
import com.ilya.profileview.profileScreen.ProfileScreenEvent
import com.ilya.profileview.profileScreen.ProfileScreenState
import com.ilya.profileview.profileScreen.ProfileScreenViewModel
import com.ilya.profileview.profileScreen.components.posts.OnEmptyPostsMessage
import com.ilya.profileview.profileScreen.components.posts.PostCard
import com.ilya.profileview.profileScreen.components.posts.ResolveAppend
import com.ilya.profileview.profileScreen.components.posts.ResolveRefresh
import com.ilya.profileview.profileScreen.components.profileCommon.Photos
import com.ilya.profileview.profileScreen.components.profileCommon.ProfileHeader
import com.ilya.profileview.profileScreen.components.profileCommon.TopBar
import com.ilya.theme.LocalColorScheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userId: Long,
    isPrivate: Boolean,
    onEmptyAccessToken: () -> Unit,
    onBackClick: () -> Unit,
    onPhotoClick: (id: Long, targetPhotoIndex: Int) -> Unit,
    onOpenPhotosClick: (Long) -> Unit,
    onPostPhotoClick: (userId: Long, targetPhotoIndex: Int, photoIds: Map<Long, String>) -> Unit,
    onVideoClick: (ownerId: Long, id: Long, accessKey: String) -> Unit
) {
    val viewModel: ProfileScreenViewModel = hiltViewModel()

    if (isPrivate) {
        PrivateProfile(viewModel, userId, onBackClick, onEmptyAccessToken)
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

    BackHandler {
        viewModel.handleEvent(ProfileScreenEvent.Back)
        onBackClick()
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = LocalColorScheme.current.primary,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopBar(
                userId = userId,
                contentOffset = scrollBehavior.state.contentOffset,
                onBackClick = {
                    viewModel.handleEvent(ProfileScreenEvent.Back)
                    onBackClick()
                }
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
                onEmptyAccessToken = onEmptyAccessToken,
                onTryAgainClick = { viewModel.handleEvent(ProfileScreenEvent.Retry) },
                padding = padding
            )

            is ProfileScreenState.Success -> {
                Box {
                    Content(
                        user = state.user,
                        posts = posts,
                        currentLoopingAudio = currentLoopingAudio,
                        friendRequest = { viewModel.handleEvent(ProfileScreenEvent.FriendRequest(it)) },
                        paddingValues = padding,
                        onPhotoClick = onPhotoClick,
                        onOpenPhotosClick = { onOpenPhotosClick(state.user.id) },
                        onLikeClick = { viewModel.handleEvent(ProfileScreenEvent.Like(it)) },
                        likes = likesState,
                        onPostPhotoClick = onPostPhotoClick,
                        onAudioClick = { viewModel.handleEvent(ProfileScreenEvent.AudioClick(it)) },
                        onVideoClick = { onVideoClick(it.ownerId, it.id, it.accessKey) }
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
                        viewModel.handleEvent(ProfileScreenEvent.PostsAdded(newLikes))
                    }
                }
            }
        }
    }

    SnackbarEventEffect(
        state = snackbarState,
        onConsumed = { viewModel.handleEvent(ProfileScreenEvent.SnackbarConsumed) },
        action = { snackbarHostState.showSnackbar(it) }
    )

    LaunchedEffect(Unit) {
        viewModel.handleEvent(ProfileScreenEvent.Start(userId))
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
    friendRequest: (User) -> Unit,
    onPhotoClick: (userId: Long, targetPhotoIndex: Int) -> Unit,
    onOpenPhotosClick: () -> Unit,
    onLikeClick: (Post) -> Unit,
    onPostPhotoClick: (userId: Long, targetPhotoIndex: Int, photoIds: Map<Long, String>) -> Unit,
    onAudioClick: (Audio) -> Unit,
    onVideoClick: (Video) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { ProfileHeader(user, friendRequest) }
        item { Photos(user.photos, onPhotoClick, onOpenPhotosClick) }
        items(count = posts.itemCount) {
            Post(
                posts = posts,
                index = it,
                onLikeClick = onLikeClick,
                likesState = likes,
                onPhotoClick = onPostPhotoClick,
                onAudioClick = onAudioClick,
                currentLoopingAudio = currentLoopingAudio,
                onVideoClick = onVideoClick
            )
        }
        item { ResolveAppend(posts) }
        item { ResolveRefresh(posts) }
        item { OnEmptyPostsMessage(posts) }
    }
}

@Composable
private fun Post(
    posts: LazyPagingItems<Post>,
    index: Int,
    currentLoopingAudio: Pair<Audio?, Boolean>,
    onLikeClick: (Post) -> Unit,
    likesState: PostsLikesState,
    onPhotoClick: (userId: Long, targetPhotoIndex: Int, photoIds: Map<Long, String>) -> Unit,
    onAudioClick: (Audio) -> Unit,
    onVideoClick: (Video) -> Unit
) {
    val post = posts[index]

    post?.let {
        PostCard(
            post = it,
            currentLoopingAudio = currentLoopingAudio,
            onLikeClick = { post -> onLikeClick(post) },
            likes = likesState.likes[post.id],
            onPhotoClick = onPhotoClick,
            onAudioClick = onAudioClick,
            onVideoClick = onVideoClick
        )
    }
}
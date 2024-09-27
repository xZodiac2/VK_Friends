package com.ilya.profileview.profileScreen

import android.media.MediaPlayer
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ilya.core.appCommon.StringResource
import com.ilya.core.appCommon.accessToken.AccessTokenManager
import com.ilya.core.appCommon.base.EventHandler
import com.ilya.core.appCommon.compose.ImmutablePair
import com.ilya.core.appCommon.compose.basicComposables.snackbar.SnackbarState
import com.ilya.core.appCommon.compose.with
import com.ilya.core.appCommon.enums.ObjectType
import com.ilya.core.util.logThrowable
import com.ilya.paging.models.Audio
import com.ilya.paging.models.Comment
import com.ilya.paging.models.LikeableCommonInfo
import com.ilya.paging.models.Likes
import com.ilya.paging.models.Post
import com.ilya.paging.pagingSources.CommentsPagingSource
import com.ilya.paging.pagingSources.PostsPagingSource
import com.ilya.profileViewDomain.User
import com.ilya.profileViewDomain.useCase.GetUserDataUseCase
import com.ilya.profileViewDomain.useCase.ResolveFriendRequestUseCase
import com.ilya.profileViewDomain.useCase.ResolveLikeUseCase
import com.ilya.profileview.R
import com.ilya.profileview.profileScreen.screens.event.ProfileScreenEvent
import com.ilya.profileview.profileScreen.screens.event.ProfileScreenNavEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okio.IOException
import javax.inject.Inject

@HiltViewModel
internal class ProfileScreenViewModel @Inject constructor(
    private val accessTokenManager: AccessTokenManager,
    private val getUserDataUseCase: GetUserDataUseCase,
    private val resolveFriendRequestUseCase: ResolveFriendRequestUseCase,
    private val resolveLikeUseCase: ResolveLikeUseCase,
    private val postsPagingSourceFactory: PostsPagingSource.Factory,
    private val commentsPagingSourceFactory: CommentsPagingSource.Factory,
    private val mediaPlayer: MediaPlayer
) : ViewModel(), EventHandler<ProfileScreenEvent> {

    private val userId = MutableStateFlow(DEFAULT_USER_ID)
    private val postId = MutableStateFlow(DEFAULT_POST_ID)

    @OptIn(ExperimentalCoroutinesApi::class)
    val postsFlow = userId.flatMapLatest { id ->
        if (id == DEFAULT_USER_ID) return@flatMapLatest flow { emit(PagingData.empty()) }
        newPostsPager(id).flow
    }.cachedIn(viewModelScope)

    private fun newPostsPager(userId: Long): Pager<Int, Post> {
        return Pager(
            config = PagingConfig(
                pageSize = POSTS_PAGE_SIZE,
                initialLoadSize = INITIAL_POSTS_LOAD_SIZE
            ),
            pagingSourceFactory = {
                postsPagingSourceFactory.newInstance(userId)
            }
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val commentsFlow = postId.flatMapLatest { id ->
        if (id == DEFAULT_POST_ID) return@flatMapLatest flow { emit(PagingData.empty()) }
        newCommentsPager(id).flow
    }.cachedIn(viewModelScope)

    private fun newCommentsPager(postId: Long): Pager<Int, Comment> {
        return Pager(
            config = PagingConfig(
                pageSize = COMMENTS_PAGE_SIZE,
                initialLoadSize = INITIAL_COMMENTS_LOAD_SIZE
            ),
            pagingSourceFactory = {
                val initData = CommentsPagingSource.InitData(
                    ownerId = userId.value,
                    postId = postId
                )
                commentsPagingSourceFactory.newInstance(initData)
            }
        )
    }

    private val commentsLikesFlow = MutableStateFlow<Map<Long, Likes>>(emptyMap())

    val bottomSheetState = combine(postId, commentsLikesFlow) { id, likes ->
        CommentsBottomSheetState(id != DEFAULT_POST_ID, commentsFlow, likes)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = CommentsBottomSheetState(false, flow { emit(PagingData.empty()) }, emptyMap())
    )

    private val _screenState = MutableStateFlow<ProfileScreenState>(ProfileScreenState.Loading)
    val screenState = _screenState.asStateFlow()

    private val _snackbarState = MutableStateFlow<SnackbarState>(SnackbarState.Consumed)
    val snackbarState = _snackbarState.asStateFlow()

    private val _postLikesState = MutableStateFlow(PostsLikesState(emptyMap()))
    val postLikesState = _postLikesState.asStateFlow()

    // current looping audio to mediaPlayer.isPlaying
    private val _currentLoopingAudioState = MutableStateFlow<ImmutablePair<Audio?, Boolean>>(null with false)
    val currentLoopingAudio = _currentLoopingAudioState.asStateFlow()

    private val _audioLoadingState = MutableStateFlow<AudioLoadIndicatorState>(AudioLoadIndicatorState.Idle)
    val audioIndicatorState = _audioLoadingState.asStateFlow()

    private val _navEventFlow = MutableSharedFlow<ProfileScreenNavEvent>()
    val navEventFlow = _navEventFlow.asSharedFlow()

    override fun handleEvent(event: ProfileScreenEvent) {
        when (event) {
            is ProfileScreenEvent.Start -> {
                userId.value = event.userId
                onStart()
            }

            is ProfileScreenEvent.FriendRequest -> onFriendRequest(event.user)
            is ProfileScreenEvent.Like -> onLike(event.item)
            is ProfileScreenEvent.PostsAdded -> onPostsAdded(event.newLikes)
            is ProfileScreenEvent.AudioClick -> onAudioClick(event.audio)
            is ProfileScreenEvent.NewNavEvent -> onNewNavEvent(event.navEvent)
            is ProfileScreenEvent.CommentsClick -> onCommentsClick(event.postId)
            is ProfileScreenEvent.CommentsAdded -> onCommentsAdded(event.newLikes)
            ProfileScreenEvent.Retry -> onRetry()
            ProfileScreenEvent.SnackbarConsumed -> onSnackbarConsumed()
            ProfileScreenEvent.Back -> onBack()
            ProfileScreenEvent.DismissBottomSheet -> onDismissBottomSheet()
        }
    }

    private fun onCommentsAdded(newLikes: Map<Long, Likes>) {
        viewModelScope.launch {
            commentsLikesFlow.emit(newLikes)
        }
    }

    private fun onDismissBottomSheet() {
        this.postId.value = DEFAULT_POST_ID
    }

    private fun onCommentsClick(postId: Long) {
        this.postId.value = postId
    }

    private fun onNewNavEvent(event: ProfileScreenNavEvent) {
        if (event is ProfileScreenNavEvent.AnotherProfileClick) {
            if (userId.value == event.id) {
                showSnackbar(R.string.you_already_on_reqiered_profile)
                return
            }
        }

        viewModelScope.launch { _navEventFlow.emit(event) }
    }

    private fun onBack() {
        mediaPlayer.reset()
    }

    private fun onAudioClick(audio: Audio) {
        viewModelScope.launch(Dispatchers.IO) {
            _audioLoadingState.value = AudioLoadIndicatorState.Loading

            if (audio.url.isEmpty()) {
                showSnackbar(R.string.error_cant_play_audio)
                _audioLoadingState.value = AudioLoadIndicatorState.Idle
                return@launch
            }

            if (audio == _currentLoopingAudioState.value.first) {
                handleCurrentAudioState()
            } else {
                val playResult = playNewAudio(audio)
                playResult.onSuccess {
                    _currentLoopingAudioState.value = audio with mediaPlayer.isPlaying
                }
            }

            _audioLoadingState.value = AudioLoadIndicatorState.Idle
        }
    }

    private fun handleCurrentAudioState() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        } else {
            mediaPlayer.start()
        }
        _currentLoopingAudioState.value = _currentLoopingAudioState.value.first with mediaPlayer.isPlaying
    }

    private fun playNewAudio(audio: Audio): Result<Unit> {
        return try {
            mediaPlayer.reset()
            prepareAndStartAudio(audio.url)
            Result.success(Unit)
        } catch (e: IOException) {
            logThrowable(e)
            showSnackbar(R.string.error_no_internet)
            Result.failure(e)
        }
    }

    private fun prepareAndStartAudio(url: String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepare()
        mediaPlayer.start()
    }

    private fun onPostsAdded(likes: Map<Long, Likes>) {
        _postLikesState.value = PostsLikesState(likes)
    }

    private fun onLike(item: LikeableCommonInfo) {
        val accessToken = accessTokenManager.accessToken ?: run {
            showSnackbar(R.string.error_cant_like)
            return
        }

        toggleLike(item).onFailure {
            showSnackbar(R.string.error_cant_like)
            return
        }

        val likesExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            logThrowable(throwable)
            toggleLike(item)
            when (throwable) {
                is IOException -> showSnackbar(R.string.error_no_internet)
                else -> showSnackbar(R.string.error_cant_like)
            }
        }

        viewModelScope.launch(Dispatchers.IO + likesExceptionHandler) {
            val result = resolveLikeUseCase(
                ResolveLikeUseCase.InvokeData(
                    info = item,
                    accessToken = accessToken.token
                )
            )

            result.onFailure {
                showSnackbar(R.string.error_cant_like)
                toggleLike(item)
            }
        }
    }

    private fun toggleLike(item: LikeableCommonInfo): Result<Unit> {
        val likesMap = when (item.objectType) {
            ObjectType.POST -> _postLikesState.value.likes.toMutableMap()
            ObjectType.COMMENT -> commentsLikesFlow.value.toMutableMap()
            else -> null
        } ?: return Result.failure(IllegalArgumentException())

        val likes = likesMap[item.id] ?: return Result.failure(IllegalArgumentException())
        likesMap[item.id] = likes.toggled()
        when (item.objectType) {
            ObjectType.POST -> _postLikesState.value = PostsLikesState(likesMap)
            ObjectType.COMMENT -> commentsLikesFlow.value = likesMap
            else -> Unit
        }
        return Result.success(Unit)
    }

    private fun Likes.toggled(): Likes {
        return this.copy(
            userLikes = !this.userLikes,
            count = if (this.userLikes) this.count - 1 else this.count + 1
        )
    }

    private fun showSnackbar(@StringRes text: Int) {
        _snackbarState.value = SnackbarState.Triggered(StringResource.FromId(text))
    }

    private fun onSnackbarConsumed() {
        _snackbarState.value = SnackbarState.Consumed
    }

    private fun onFriendRequest(user: User) {
        val friendRequestExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            toggleFriend()
            logThrowable(throwable)
            when (throwable) {
                is IOException -> showSnackbar(R.string.error_no_internet)
                else -> showSnackbar(R.string.operation_not_completed_try_later)
            }
        }

        val accessToken = accessTokenManager.accessToken ?: run {
            _screenState.value = ProfileScreenState.Error(ErrorType.NoAccessToken)
            return
        }

        toggleFriend().onFailure {
            showSnackbar(R.string.operation_not_completed_try_later)
            return
        }

        viewModelScope.launch(Dispatchers.IO + friendRequestExceptionHandler) {
            resolveFriendRequestUseCase(
                ResolveFriendRequestUseCase.InvokeData(
                    accessToken = accessToken.token,
                    user = user
                )
            )
        }
    }

    private fun toggleFriend(): Result<Unit> {
        val state = _screenState.value as? ProfileScreenState.ViewData ?: return Result.failure(IllegalStateException())

        _screenState.value = state.copy(user = state.user.copy(friendStatus = state.user.friendStatus.toggled()))
        return Result.success(Unit)
    }

    private fun onRetry() {
        _screenState.value = ProfileScreenState.Loading
        onStart()
    }

    private fun onStart() {
        if (_screenState.value is ProfileScreenState.ViewData) return

        val getDataExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            logThrowable(throwable)
            _screenState.value = when (throwable) {
                is IOException -> ProfileScreenState.Error(ErrorType.NoInternet)
                else -> ProfileScreenState.Error(ErrorType.Unknown(throwable))
            }
        }

        val accessToken = accessTokenManager.accessToken ?: run {
            _screenState.value = ProfileScreenState.Error(ErrorType.NoAccessToken)
            return
        }

        viewModelScope.launch(Dispatchers.IO + getDataExceptionHandler) {
            val userData = getUserDataUseCase(
                GetUserDataUseCase.InvokeData(
                    accessToken = accessToken.token,
                    userId = userId.value
                )
            )

            _screenState.value = ProfileScreenState.ViewData(
                user = when {
                    userId.value == accessToken.userID -> userData.copy(isAccountOwner = true)
                    else -> userData
                }
            )
        }
    }

    companion object {
        private const val DEFAULT_USER_ID: Long = -1
        private const val DEFAULT_POST_ID: Long = -1
        private const val POSTS_PAGE_SIZE = 3
        private const val INITIAL_POSTS_LOAD_SIZE = 3
        private const val COMMENTS_PAGE_SIZE = 10
        private const val INITIAL_COMMENTS_LOAD_SIZE = 10
    }

}
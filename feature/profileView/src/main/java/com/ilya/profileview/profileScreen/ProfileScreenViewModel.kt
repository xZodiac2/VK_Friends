package com.ilya.profileview.profileScreen

import android.media.MediaPlayer
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ilya.core.appCommon.AccessTokenManager
import com.ilya.core.appCommon.StringResource
import com.ilya.core.appCommon.enums.toggled
import com.ilya.core.basicComposables.snackbar.SnackbarState
import com.ilya.core.util.logThrowable
import com.ilya.profileViewDomain.models.Audio
import com.ilya.profileViewDomain.models.Likes
import com.ilya.profileViewDomain.models.Post
import com.ilya.profileViewDomain.models.User
import com.ilya.profileViewDomain.models.toggled
import com.ilya.profileViewDomain.useCase.GetPostsPagingFlowUseCase
import com.ilya.profileViewDomain.useCase.GetUserDataUseCase
import com.ilya.profileViewDomain.useCase.ResolveFriendRequestUseCase
import com.ilya.profileViewDomain.useCase.ResolveLikeUseCase
import com.ilya.profileview.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import okio.IOException
import javax.inject.Inject

@HiltViewModel
internal class ProfileScreenViewModel @Inject constructor(
    private val accessTokenManager: AccessTokenManager,
    private val getUserDataUseCase: GetUserDataUseCase,
    private val resolveFriendRequestUseCase: ResolveFriendRequestUseCase,
    private val getPostsPagingFlowUseCase: GetPostsPagingFlowUseCase,
    private val resolveLikeUseCase: ResolveLikeUseCase,
    private val mediaPlayer: MediaPlayer
) : ViewModel() {

    private val userId = MutableStateFlow(DEFAULT_USER_ID)

    @OptIn(ExperimentalCoroutinesApi::class)
    val postsFlow: Flow<PagingData<Post>> = userId.flatMapLatest { id ->
        if (id == DEFAULT_USER_ID) {
            return@flatMapLatest flow { emit(PagingData.empty()) }
        }
        getPostsPagingFlowUseCase(
            GetPostsPagingFlowUseCase.InvokeData(
                config = PagingConfig(
                    pageSize = PAGE_SIZE,
                    initialLoadSize = INITIAL_LOAD_SIZE
                ),
                userId = id
            )
        )
    }.cachedIn(viewModelScope)

    private val _screenState = MutableStateFlow<ProfileScreenState>(ProfileScreenState.Loading)
    val screenState = _screenState.asStateFlow()

    private val _snackbarState = MutableStateFlow<SnackbarState>(SnackbarState.Consumed)
    val snackbarState = _snackbarState.asStateFlow()

    private val _likesState = MutableStateFlow(PostsLikesState(emptyMap()))
    val likesState = _likesState.asStateFlow()

    // current looping audio to mediaPlayer.isPlaying
    private val _currentLoopingAudioState =
        MutableStateFlow<Pair<Audio?, Boolean>>(null to false)
    val currentLoopingAudio = _currentLoopingAudioState.asStateFlow()

    private val _audioLoadingState =
        MutableStateFlow<AudioLoadIndicatorState>(AudioLoadIndicatorState.Idle)
    val audioIndicatorState = _audioLoadingState.asStateFlow()

    fun handleEvent(event: ProfileScreenEvent) {
        when (event) {
            is ProfileScreenEvent.Start -> {
                userId.value = event.userId
                onStart()
            }

            is ProfileScreenEvent.FriendRequest -> onFriendRequest(event.user)
            is ProfileScreenEvent.Like -> onLike(event.post)
            is ProfileScreenEvent.PostsAdded -> onPostsAdded(event.newLikes)
            is ProfileScreenEvent.AudioClick -> onAudioClick(event.audio)
            ProfileScreenEvent.Retry -> onRetry()
            ProfileScreenEvent.SnackbarConsumed -> onSnackbarConsumed()
            ProfileScreenEvent.Back -> onBack()
        }
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
                    _currentLoopingAudioState.value = audio to mediaPlayer.isPlaying
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
        _currentLoopingAudioState.value = _currentLoopingAudioState.value.first to mediaPlayer.isPlaying
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
        _likesState.value = PostsLikesState(_likesState.value.likes + likes)
    }

    private fun onLike(post: Post) {
        val accessToken = accessTokenManager.accessToken ?: run {
            showSnackbar(R.string.error_cant_like)
            return
        }

        toggleLike(post.id).onFailure {
            showSnackbar(R.string.error_cant_like)
            return
        }

        val likesExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            logThrowable(throwable)
            toggleLike(post.id)
            when (throwable) {
                is IOException -> showSnackbar(R.string.error_no_internet)
                else -> showSnackbar(R.string.error_cant_like)
            }
        }

        viewModelScope.launch(Dispatchers.IO + likesExceptionHandler) {
            val result = resolveLikeUseCase(
                ResolveLikeUseCase.InvokeData(
                    likeable = post,
                    accessToken = accessToken.token
                )
            )

            result.onFailure {
                showSnackbar(R.string.error_cant_like)
                toggleLike(post.id)
            }
        }
    }

    private fun toggleLike(postId: Long): Result<Unit> {
        val likesMap = _likesState.value.likes.toMutableMap()
        val likes = likesMap[postId] ?: return Result.failure(IllegalArgumentException())
        likesMap[postId] = likes.toggled()
        _likesState.value = PostsLikesState(likesMap)
        return Result.success(Unit)
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
        val state = _screenState.value as? ProfileScreenState.Success
            ?: return Result.failure(IllegalStateException())

        _screenState.value = state.copy(
            user = state.user.copy(friendStatus = state.user.friendStatus.toggled())
        )
        return Result.success(Unit)
    }

    private fun onRetry() {
        _screenState.value = ProfileScreenState.Loading
        onStart()
    }

    private fun onStart() {
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

            _screenState.value = ProfileScreenState.Success(
                user = when {
                    userId.value == accessToken.userID -> userData.copy(isAccountOwner = true)
                    else -> userData
                }
            )
        }
    }

    companion object {
        private const val DEFAULT_USER_ID: Long = -1
        private const val PAGE_SIZE = 3
        private const val INITIAL_LOAD_SIZE = 3
    }

}
package com.ilya.profileview.videoPreview

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ilya.core.appCommon.StringResource
import com.ilya.core.appCommon.accessToken.AccessTokenManager
import com.ilya.core.basicComposables.snackbar.SnackbarState
import com.ilya.core.util.logThrowable
import com.ilya.paging.models.Likes
import com.ilya.paging.models.VideoExtended
import com.ilya.paging.models.toggled
import com.ilya.profileViewDomain.useCase.GetVideoUseCase
import com.ilya.profileViewDomain.useCase.ResolveLikeUseCase
import com.ilya.profileview.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okio.IOException
import javax.inject.Inject

@HiltViewModel
internal class VideoPreviewViewModel @Inject constructor(
    private val accessTokenManager: AccessTokenManager,
    private val resolveLikeUseCase: ResolveLikeUseCase,
    private val getVideoUseCase: GetVideoUseCase
) : ViewModel() {

    private val _videoState = MutableStateFlow<VideoExtended?>(null)
    val videoState = _videoState.asStateFlow()

    private val _likesState = MutableStateFlow<Likes?>(null)
    val likesState = _likesState.asStateFlow()

    private val _snackbarState = MutableStateFlow<SnackbarState>(SnackbarState.Consumed)
    val snackbarState = _snackbarState.asStateFlow()

    fun handleEvent(event: VideoPreviewEvent) {
        when (event) {
            is VideoPreviewEvent.Start -> onStart(event.ownerId, event.videoId, event.accessKey)
            is VideoPreviewEvent.Like -> onLike(event.video)
            VideoPreviewEvent.SnackbarConsumed -> onSnackbarConsumed()
        }
    }

    private fun onSnackbarConsumed() {
        _snackbarState.value = SnackbarState.Consumed
    }

    private fun onStart(ownerId: Long, videoId: Long, accessKey: String) {
        val getVideoExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            logThrowable(throwable)
            when (throwable) {
                is IOException -> showSnackbar(R.string.error_no_internet)
                else -> showSnackbar(R.string.error_try_later)
            }
        }

        val accessToken = accessTokenManager.accessToken ?: run {
            showSnackbar(R.string.error_unknown)
            return
        }

        viewModelScope.launch(Dispatchers.IO + getVideoExceptionHandler) {
            val video = getVideoUseCase(
                GetVideoUseCase.InvokeData(
                    accessToken = accessToken.token,
                    ownerId = ownerId,
                    videoId = videoId,
                    accessKey = accessKey
                )
            )
            _videoState.value = video
            _likesState.value = video.likes
        }

    }

    private fun onLike(video: VideoExtended?) {
        video ?: run {
            showSnackbar(R.string.error_cant_like)
            return
        }

        val likeExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            logThrowable(throwable)
            toggleLike()
            when (throwable) {
                is IOException -> showSnackbar(R.string.error_no_internet)
                else -> showSnackbar(R.string.error_cant_like)
            }
        }

        val accessToken = accessTokenManager.accessToken ?: run {
            showSnackbar(R.string.error_cant_like)
            return
        }

        val toggleResult = toggleLike()
        toggleResult.onFailure {
            showSnackbar(R.string.error_cant_like)
            return
        }

        viewModelScope.launch(Dispatchers.IO + likeExceptionHandler) {
            val result = resolveLikeUseCase(
                ResolveLikeUseCase.InvokeData(
                    likeable = video.copy(likes = _likesState.value?.toggled()),
                    accessToken = accessToken.token
                )
            )

            result.onFailure {
                showSnackbar(R.string.error_cant_like)
                toggleLike()
            }
        }

    }

    private fun toggleLike(): Result<Unit> {
        val like = _likesState.value ?: return Result.failure(IllegalStateException())
        _likesState.value = like.toggled()
        return Result.success(Unit)
    }

    private fun showSnackbar(@StringRes text: Int) {
        _snackbarState.value = SnackbarState.Triggered(StringResource.FromId(text))
    }

}
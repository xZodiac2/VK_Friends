package com.ilya.profileview.presentation.photosPreview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ilya.core.appCommon.AccessTokenManager
import com.ilya.core.appCommon.StringResource
import com.ilya.core.basicComposables.snackbar.SnackbarState
import com.ilya.core.util.logThrowable
import com.ilya.profileViewDomain.models.Likes
import com.ilya.profileViewDomain.models.Photo
import com.ilya.profileViewDomain.useCase.GetPhotosPagingFlowUseCase
import com.ilya.profileViewDomain.useCase.GetPhotosPagingFlowUseCaseInvokeData
import com.ilya.profileViewDomain.useCase.ResolveLikeUseCase
import com.ilya.profileViewDomain.useCase.ResolveLikeUseCaseInvokeData
import com.ilya.profileview.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotosPreviewViewModel @Inject constructor(
    private val resolveLikeUseCase: ResolveLikeUseCase,
    private val accessTokenManager: AccessTokenManager,
    private val getPhotosPagingFlowUseCase: GetPhotosPagingFlowUseCase,
) : ViewModel() {

    private val idsFlow = MutableStateFlow<IdsState?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val photosFlow = idsFlow
        .flatMapLatest { ids ->
            ids ?: return@flatMapLatest flow { emit(PagingData.empty()) }
            getPhotosPagingFlowUseCase(
                GetPhotosPagingFlowUseCaseInvokeData(
                    pagingConfig = PagingConfig(
                        pageSize = PAGE_SIZE,
                        initialLoadSize = INITIAL_LOAD_SIZE,
                    ),
                    userId = ids.userId,
                    isPreview = true,
                    targetPhotoIndex = ids.targetPhotoIndex
                )
            )
        }.cachedIn(viewModelScope)

    private val _likesState = MutableStateFlow(LikesState(emptyMap()))
    val likesState = _likesState.asStateFlow()

    private val _snackbarState = MutableStateFlow<SnackbarState>(SnackbarState.Consumed)
    val snackbarState = _snackbarState.asStateFlow()

    private val likesExceptionHandler = CoroutineExceptionHandler { _, e ->
        logThrowable(e)
        showSnackbarError()
    }

    fun handleEvent(event: PhotosPreviewEvent) {
        when (event) {
            is PhotosPreviewEvent.Start -> onStart(event.userId, event.targetPhotoIndex)
            PhotosPreviewEvent.SnackbarConsumed -> onSnackbarConsumed()
            is PhotosPreviewEvent.Like -> onLike(event.photo)
            is PhotosPreviewEvent.PhotosAdded -> onPhotosAdded(event.likes)
        }
    }

    private fun onPhotosAdded(likes: Map<Long, Likes>) {
        _likesState.value = LikesState(likes)
    }

    private fun onStart(userId: Long, targetPhotoIndex: Int) {
        idsFlow.value = IdsState(userId, targetPhotoIndex)
    }

    private fun onSnackbarConsumed() {
        _snackbarState.value = SnackbarState.Consumed
    }

    private fun onLike(photo: Photo?) {
        photo ?: run {
            showSnackbarError()
            return
        }

        val accessToken = accessTokenManager.accessToken?.token ?: run {
            showSnackbarError()
            return
        }

        viewModelScope.launch(Dispatchers.IO + likesExceptionHandler) {
            val result = resolveLikeUseCase(
                data = ResolveLikeUseCaseInvokeData(
                    accessToken = accessToken,
                    attachment = photo
                )
            )

            result.fold(
                onFailure = {
                    showSnackbarError()
                },
                onSuccess = {
                    updateState(photo.id to it)
                }
            )
        }
    }

    private fun updateState(pair: Pair<Long, Likes>) {
        val photoId = pair.first
        val likes = pair.second

        val likesMap = _likesState.value.likes.toMutableMap()
        likesMap[photoId] = likes

        _likesState.value = LikesState(likesMap)
    }

    private fun showSnackbarError() {
        _snackbarState.value = SnackbarState.Triggered(
            text = StringResource.FromId(R.string.error_cant_like)
        )
    }

    private data class IdsState(
        val userId: Long,
        val targetPhotoIndex: Int
    )

    companion object {
        const val PAGE_SIZE = 20
        private const val INITIAL_LOAD_SIZE = 20
    }

}

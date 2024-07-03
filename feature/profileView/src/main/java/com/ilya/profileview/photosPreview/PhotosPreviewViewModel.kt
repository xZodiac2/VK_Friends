package com.ilya.profileview.photosPreview

import androidx.annotation.StringRes
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
import com.ilya.profileViewDomain.useCase.GetPhotosUseCase
import com.ilya.profileViewDomain.useCase.ResolveLikeUseCase
import com.ilya.profileViewDomain.useCase.ResolveLikeUseCaseInvokeData
import com.ilya.profileview.R
import com.ilya.profileview.photosPreview.states.PhotosLikesState
import com.ilya.profileview.photosPreview.states.RestrainedPhotosState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import okio.IOException
import javax.inject.Inject

@HiltViewModel
internal class PhotosPreviewViewModel @Inject constructor(
    private val resolveLikeUseCase: ResolveLikeUseCase,
    private val accessTokenManager: AccessTokenManager,
    private val getPhotosPagingFlowUseCase: GetPhotosPagingFlowUseCase,
    private val getPhotosUseCase: GetPhotosUseCase
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

    private val _photosState = MutableStateFlow<RestrainedPhotosState>(RestrainedPhotosState.Loading)
    val photosState = _photosState.asStateFlow()

    private val _likesState = MutableStateFlow(PhotosLikesState(emptyMap()))
    val likesState = _likesState.asStateFlow()

    private val _snackbarState = MutableStateFlow<SnackbarState>(SnackbarState.Consumed)
    val snackbarState = _snackbarState.asStateFlow()

    fun handleEvent(event: PhotosPreviewEvent) {
        when (event) {
            is PhotosPreviewEvent.Start -> onStart(event.userId, event.targetPhotoIndex)
            PhotosPreviewEvent.SnackbarConsumed -> onSnackbarConsumed()
            is PhotosPreviewEvent.RestrainedStart -> onRestrainedStart(event.userId, event.photoIds)
            is PhotosPreviewEvent.Like -> onLike(event.photo)
            is PhotosPreviewEvent.PhotosAdded -> onPhotosAdded(event.likes)
        }
    }

    private fun onRestrainedStart(userId: Long, photoIds: Map<Long, String>) {
        val photosExceptionHandler = CoroutineExceptionHandler { _, e ->
            logThrowable(e)
            when (e) {
                is IOException -> showSnackbar(R.string.error_no_internet)
                else -> showSnackbar(R.string.error_cant_open_photos)
            }
        }

        val accessToken = accessTokenManager.accessToken ?: run {
            showSnackbar(R.string.error_cant_open_photos)
            return
        }

        viewModelScope.launch(Dispatchers.IO + photosExceptionHandler) {
            val photos = getPhotosUseCase(
                GetPhotosUseCase.InvokeData(
                    accessToken = accessToken.token,
                    userId = userId,
                    photoIds = photoIds
                )
            )

            val likes = photos.mapNotNull { it.likes?.let { likes -> it.id to likes } }.toMap()
            _photosState.value = RestrainedPhotosState.ShowPhotos(photos)
            _likesState.value = PhotosLikesState(likes)
        }
    }

    private fun onPhotosAdded(likes: Map<Long, Likes>) {
        _likesState.value = PhotosLikesState(likes)
    }

    private fun onStart(userId: Long, targetPhotoIndex: Int) {
        idsFlow.value = IdsState(userId, targetPhotoIndex)
    }

    private fun onSnackbarConsumed() {
        _snackbarState.value = SnackbarState.Consumed
    }

    private fun onLike(photo: Photo?) {
        photo ?: run {
            showSnackbar(R.string.error_cant_like)
            rollbackLikesState()
            return
        }

        val accessToken = accessTokenManager.accessToken?.token ?: run {
            showSnackbar(R.string.error_cant_like)
            rollbackLikesState()
            return
        }

        val likesExceptionHandler = CoroutineExceptionHandler { _, e ->
            logThrowable(e)
            showSnackbar(R.string.error_cant_like)
            rollbackLikesState()
        }
        
        viewModelScope.launch(Dispatchers.IO + likesExceptionHandler) {
            val result = resolveLikeUseCase(
                data = ResolveLikeUseCaseInvokeData(
                    accessToken = accessToken,
                    likeable = photo
                )
            )

            result.fold(
                onFailure = {
                    showSnackbar(R.string.error_cant_like)
                    rollbackLikesState()
                },
                onSuccess = { updateLikesState(photo.id to it) }
            )
        }
    }

    private fun updateLikesState(pair: Pair<Long, Likes>) {
        val photoId = pair.first
        val likes = pair.second

        val likesMap = _likesState.value.likes.toMutableMap()
        likesMap[photoId] = likes

        _likesState.value = PhotosLikesState(likesMap)
    }

    private fun rollbackLikesState() {
        _likesState.value = PhotosLikesState(_likesState.value.likes)
    }

    private fun showSnackbar(@StringRes text: Int) {
        _snackbarState.value = SnackbarState.Triggered(StringResource.FromId(text))
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

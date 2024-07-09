package com.ilya.profileview.photosPreview

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ilya.core.appCommon.AccessTokenManager
import com.ilya.core.appCommon.StringResource
import com.ilya.core.basicComposables.snackbar.SnackbarState
import com.ilya.core.util.logThrowable
import com.ilya.paging.Likes
import com.ilya.paging.PaginationError
import com.ilya.paging.Photo
import com.ilya.paging.pagingSources.PhotosPagingSource
import com.ilya.paging.toggled
import com.ilya.profileViewDomain.useCase.GetPhotosUseCase
import com.ilya.profileViewDomain.useCase.ResolveLikeUseCase
import com.ilya.profileview.R
import com.ilya.profileview.photosPreview.states.PhotosLikesState
import com.ilya.profileview.photosPreview.states.PhotosPreviewNavState
import com.ilya.profileview.photosPreview.states.RestrainedPhotosState
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
internal class PhotosPreviewViewModel @Inject constructor(
    private val resolveLikeUseCase: ResolveLikeUseCase,
    private val accessTokenManager: AccessTokenManager,
    private val photosPagingSourceFactory: PhotosPagingSource.Factory,
    private val getPhotosUseCase: GetPhotosUseCase
) : ViewModel() {

    private val idsFlow = MutableStateFlow<IdsState?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val photosFlow: Flow<PagingData<Photo>> = idsFlow
        .flatMapLatest {
            it ?: return@flatMapLatest flow { emit(PagingData.empty()) }
            newPager(it).flow
        }
        .cachedIn(viewModelScope)

    private val _photosState =
        MutableStateFlow<RestrainedPhotosState>(RestrainedPhotosState.Loading)
    val photosState = _photosState.asStateFlow()

    private val _likesState = MutableStateFlow(PhotosLikesState(emptyMap()))
    val likesState = _likesState.asStateFlow()

    private val _snackbarState = MutableStateFlow<SnackbarState>(SnackbarState.Consumed)
    val snackbarState = _snackbarState.asStateFlow()

    private val _navState = MutableStateFlow<PhotosPreviewNavState>(PhotosPreviewNavState.This)
    val navState = _navState.asStateFlow()

    private fun newPager(ids: IdsState): Pager<Int, Photo> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
            ),
            initialKey = (ids.targetPhotoIndex - (ids.targetPhotoIndex % PAGE_SIZE)) / PAGE_SIZE,
            pagingSourceFactory = {
                val initData = PhotosPagingSource.InitData(
                    userId = ids.userId,
                    isPreview = true
                )

                photosPagingSourceFactory.newInstance(initData)
            }
        )
    }

    fun handleEvent(event: PhotosPreviewEvent) {
        when (event) {
            is PhotosPreviewEvent.Start -> onStart(event.userId, event.targetPhotoIndex)
            PhotosPreviewEvent.SnackbarConsumed -> onSnackbarConsumed()
            is PhotosPreviewEvent.RestrainedStart -> onRestrainedStart(event.userId, event.photoIds)
            is PhotosPreviewEvent.Like -> onLike(event.photo)
            is PhotosPreviewEvent.PhotosAdded -> onPhotosAdded(event.likes)
            is PhotosPreviewEvent.Error -> onError(event.error)
        }
    }

    private fun onError(error: Throwable?) {
        error ?: return

        when (error) {
            is PaginationError.NoInternet -> showSnackbar(R.string.error_no_internet)
            is PaginationError.NoAccessToken -> _navState.value = PhotosPreviewNavState.AuthScreen
            else -> showSnackbar(R.string.error_unknown)
        }
    }

    private fun onRestrainedStart(userId: Long, photoIds: Map<Long, String>) {
        val photosExceptionHandler = CoroutineExceptionHandler { _, e ->
            logThrowable(e)
            when (e) {
                is IOException -> showSnackbar(R.string.error_no_internet)
                else -> showSnackbar(R.string.error_try_later)
            }
        }

        val accessToken = accessTokenManager.accessToken ?: run {
            _navState.value = PhotosPreviewNavState.AuthScreen
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
        _likesState.value = PhotosLikesState(_likesState.value.likes + likes)
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
            return
        }

        val accessToken = accessTokenManager.accessToken?.token ?: run {
            showSnackbar(R.string.error_cant_like)
            return
        }

        val likesExceptionHandler = CoroutineExceptionHandler { _, e ->
            logThrowable(e)
            showSnackbar(R.string.error_cant_like)
            toggleLike(photo.id)
        }

        toggleLike(photo.id).onFailure {
            showSnackbar(R.string.error_cant_like)
            return
        }

        viewModelScope.launch(Dispatchers.IO + likesExceptionHandler) {
            val result = resolveLikeUseCase(
                data = ResolveLikeUseCase.InvokeData(
                    accessToken = accessToken,
                    likeable = photo
                )
            )

            result.onFailure {
                showSnackbar(R.string.error_cant_like)
                toggleLike(photo.id)
            }
        }
    }

    private fun toggleLike(photoId: Long): Result<Unit> {
        val likesMap = _likesState.value.likes.toMutableMap()
        val likes = likesMap[photoId]
            ?: return Result.failure(IllegalArgumentException())
        likesMap[photoId] = likes.toggled()
        _likesState.value = PhotosLikesState(likesMap)
        return Result.success(Unit)
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
    }

}

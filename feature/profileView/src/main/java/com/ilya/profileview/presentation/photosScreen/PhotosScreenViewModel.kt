package com.ilya.profileview.presentation.photosScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ilya.profileViewDomain.models.Photo
import com.ilya.profileViewDomain.useCase.GetPhotosPagingFlowUseCase
import com.ilya.profileViewDomain.useCase.GetPhotosPagingFlowUseCaseInvokeData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
internal class PhotosScreenViewModel @Inject constructor(
    private val getPhotosPagingFlowUseCase: GetPhotosPagingFlowUseCase,
) : ViewModel() {

    private var userId = MutableStateFlow(DEFAULT_USER_ID)

    @OptIn(ExperimentalCoroutinesApi::class)
    val photosFlow: Flow<PagingData<Photo>> = userId.flatMapLatest { id ->
        if (id == DEFAULT_USER_ID) {
            return@flatMapLatest flow { emit(PagingData.empty()) }
        }
        getPhotosPagingFlowUseCase(
            GetPhotosPagingFlowUseCaseInvokeData(
                pagingConfig = PagingConfig(
                    pageSize = PAGE_SIZE,
                    initialLoadSize = INITIAL_LOAD_SIZE
                ),
                userId = id,
                isPreview = false
            )
        )
    }.cachedIn(viewModelScope)

    fun handleEvent(event: PhotosScreenEvent) {
        when (event) {
            is PhotosScreenEvent.Start -> onStart(event.userId)
        }
    }

    private fun onStart(userId: Long) {
        this.userId.value = userId
    }

    companion object {
        private const val DEFAULT_USER_ID: Long = -1
        private const val PAGE_SIZE = 50
        private const val INITIAL_LOAD_SIZE = 50
    }

}
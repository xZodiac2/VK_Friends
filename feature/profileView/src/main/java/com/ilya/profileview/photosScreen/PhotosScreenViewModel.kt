package com.ilya.profileview.photosScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ilya.paging.Photo
import com.ilya.paging.pagingSources.PhotosPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
internal class PhotosScreenViewModel @Inject constructor(
    private val photosPagingSourceFactory: PhotosPagingSource.Factory,
) : ViewModel() {

    private var userId = MutableStateFlow(DEFAULT_USER_ID)

    @OptIn(ExperimentalCoroutinesApi::class)
    val photosFlow: Flow<PagingData<Photo>> = userId.flatMapLatest { id ->
        if (id == DEFAULT_USER_ID) {
            return@flatMapLatest flow { emit(PagingData.empty()) }
        }
        newPager(id).flow
    }.cachedIn(viewModelScope)

    private fun newPager(userId: Long): Pager<Int, Photo> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
            ),
            pagingSourceFactory = {
                val initData = PhotosPagingSource.InitData(
                    userId = userId,
                    isPreview = false
                )

                photosPagingSourceFactory.newInstance(initData)
            }
        )
    }

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
    }

}
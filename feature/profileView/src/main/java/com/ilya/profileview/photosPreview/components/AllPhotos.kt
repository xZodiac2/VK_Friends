package com.ilya.profileview.photosPreview.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.SubcomposeAsyncImage
import com.ilya.core.appCommon.enums.PhotoSize
import com.ilya.core.basicComposables.snackbar.SnackbarEventEffect
import com.ilya.paging.models.Likes
import com.ilya.paging.models.Photo
import com.ilya.profileview.photosPreview.PhotosPreviewViewModel
import com.ilya.profileview.photosPreview.event.PhotosPreviewEvent
import com.ilya.profileview.photosPreview.states.PhotosPreviewNavState
import kotlinx.coroutines.flow.combine

@Composable
internal fun AllPhotosPreview(
    viewModel: PhotosPreviewViewModel,
    userId: Long,
    targetPhotoIndex: Int,
    onBackClick: () -> Unit,
    navigateToAuth: () -> Unit
) {
    val photos = viewModel.photosFlow.collectAsLazyPagingItems()
    val likesState by viewModel.likesState.collectAsState()
    val snackbarState by viewModel.snackbarState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val navState by viewModel.navState.collectAsState()

    if (navState == PhotosPreviewNavState.AuthScreen) {
        navigateToAuth()
    }

    LaunchedEffect(Unit) {
        viewModel.handleEvent(PhotosPreviewEvent.Start(userId, targetPhotoIndex))
    }

    val pagerState = rememberPagerState(
        initialPage = targetPhotoIndex,
        pageCount = { photos.itemCount }
    )
    var likes by remember { mutableStateOf<Likes?>(null) }
    var currentPhoto by remember { mutableStateOf<Photo?>(null) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            PreviewTopBar(
                onBackClick = onBackClick,
                photosCount = photos.itemCount,
                currentPage = pagerState.currentPage
            )
        },
        bottomBar = {
            PreviewBottomBar(
                likes = likes,
                currentPhoto = currentPhoto,
                onLikeClick = { viewModel.handleEvent(PhotosPreviewEvent.Like(it)) }
            )
        },
        containerColor = Color.Black
    ) { padding ->
        if (photos.itemCount == 0) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.White)
            }
        } else {
            HorizontalPager(
                modifier = Modifier.padding(padding),
                state = pagerState
            ) { page ->
                val photo = photos.getOrNull(page)
                photo?.let {
                    SubcomposeAsyncImage(
                        modifier = Modifier.fillMaxSize(),
                        model = photo.sizes.find { it.type == PhotoSize.X }?.url,
                        contentDescription = "photo",
                        loading = { PhotoLoading() }
                    )
                } ?: PhotoLoading()
            }
        }

        LaunchedEffect(pagerState) {
            val snapshotFlow = combine(
                snapshotFlow { pagerState.currentPage },
                snapshotFlow { photos.loadState },
                snapshotFlow { likesState }
            ) { page, _, _ -> page }

            snapshotFlow.collect {
                currentPhoto = photos.getOrNull(it)
                likes = currentPhoto?.id?.let { id -> likesState.likes[id] }
            }
        }

        LaunchedEffect(Unit) {
            snapshotFlow { photos.itemSnapshotList.items }.collect { items ->
                val likesMap = items
                    .mapNotNull { photo ->
                        photo.likes?.let { likes -> photo.id to likes }
                    }
                    .filterNot { (photoId, _) -> photoId in likesState.likes.keys }
                    .toMap()

                viewModel.handleEvent(PhotosPreviewEvent.PhotosAdded(likesMap))
            }
        }

        LaunchedEffect(Unit) {
            val snapshotFlow = combine(
                snapshotFlow { photos.loadState.append },
                snapshotFlow { photos.loadState.refresh }
            ) { append, refresh -> append to refresh }

            snapshotFlow.collect { (append, refresh) ->
                if (append is LoadState.Error || refresh is LoadState.Error) {
                    val error = (append as? LoadState.Error)?.error ?: (refresh as LoadState.Error).error
                    viewModel.handleEvent(PhotosPreviewEvent.Error(error))
                }
            }
        }
    }

    SnackbarEventEffect(
        state = snackbarState,
        action = { snackbarHostState.showSnackbar(it) },
        onConsumed = { viewModel.handleEvent(PhotosPreviewEvent.SnackbarConsumed) }
    )
}

@Composable
private fun PhotoLoading() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = Color.White)
    }
}

private fun <T : Any> LazyPagingItems<T>.getOrNull(index: Int): T? {
    return try {
        this[index]
    } catch (e: IndexOutOfBoundsException) {
        null
    }
}
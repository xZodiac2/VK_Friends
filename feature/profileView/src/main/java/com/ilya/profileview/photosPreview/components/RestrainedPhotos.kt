package com.ilya.profileview.photosPreview.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import coil.compose.SubcomposeAsyncImage
import com.ilya.core.appCommon.enums.PhotoSize
import com.ilya.core.basicComposables.snackbar.SnackbarEventEffect
import com.ilya.paging.Likes
import com.ilya.paging.Photo
import com.ilya.profileview.photosPreview.PhotosPreviewEvent
import com.ilya.profileview.photosPreview.PhotosPreviewViewModel
import com.ilya.profileview.photosPreview.states.PhotosPreviewNavState
import com.ilya.profileview.photosPreview.states.RestrainedPhotosState
import kotlinx.coroutines.flow.combine

@Composable
internal fun RestrainedPhotosPreview(
    viewModel: PhotosPreviewViewModel,
    userId: Long,
    targetPhotoIndex: Int,
    photoIds: Map<Long, String>,
    onBackClick: () -> Unit,
    navigateToAuth: () -> Unit
) {
    val photosState = viewModel.photosState.collectAsState()
    val likesState by viewModel.likesState.collectAsState()

    var currentPage by remember { mutableIntStateOf(targetPhotoIndex) }
    var currentPhoto by remember { mutableStateOf<Photo?>(null) }
    var likes by remember { mutableStateOf<Likes?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarState by viewModel.snackbarState.collectAsState()
    val navState by viewModel.navState.collectAsState()

    if (navState == PhotosPreviewNavState.AuthScreen) {
        navigateToAuth()
    }

    LaunchedEffect(Unit) {
        viewModel.handleEvent(PhotosPreviewEvent.RestrainedStart(userId, targetPhotoIndex, photoIds))
    }

    SnackbarEventEffect(
        state = snackbarState,
        onConsumed = { viewModel.handleEvent(PhotosPreviewEvent.SnackbarConsumed) },
        action = { snackbarHostState.showSnackbar(it) }
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            val stateValue = photosState.value as? RestrainedPhotosState.ShowPhotos
            PreviewTopBar(
                onBackClick = onBackClick,
                photosCount = stateValue?.photos?.size ?: 0,
                currentPage = currentPage
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
        when (val stateValue = photosState.value) {
            RestrainedPhotosState.Loading -> OnLoading(padding)
            is RestrainedPhotosState.ShowPhotos -> {
                val pagerState = rememberPagerState(
                    initialPage = targetPhotoIndex,
                    pageCount = { stateValue.photos.size }
                )

                HorizontalPager(pagerState) { page ->
                    val photo = stateValue.photos[page]
                    SubcomposeAsyncImage(
                        modifier = Modifier.fillMaxSize(),
                        model = photo.sizes.find { it.type == PhotoSize.X }?.url,
                        contentDescription = "photo",
                        loading = { OnLoading(padding) }
                    )
                }

                LaunchedEffect(pagerState) {
                    val snapshotFlow = combine(
                        snapshotFlow { pagerState.currentPage },
                        snapshotFlow { likesState }
                    ) { page, _ -> page }

                    snapshotFlow.collect {
                        currentPage = it
                        currentPhoto = stateValue.photos[it]
                        likes = currentPhoto?.id?.let { id -> likesState.likes[id] }
                    }
                }
            }
        }


    }

}

@Composable
private fun OnLoading(padding: PaddingValues) {
    Box(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Color.White)
    }
}

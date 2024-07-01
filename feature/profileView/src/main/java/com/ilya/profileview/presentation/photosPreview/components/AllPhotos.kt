package com.ilya.profileview.presentation.photosPreview.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.SubcomposeAsyncImage
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ilya.core.appCommon.enums.PhotoSize
import com.ilya.core.basicComposables.snackbar.SnackbarEventEffect
import com.ilya.profileViewDomain.models.Likes
import com.ilya.profileViewDomain.models.Photo
import com.ilya.profileview.R
import com.ilya.profileview.presentation.photosPreview.PhotosPreviewEvent
import com.ilya.profileview.presentation.photosPreview.PhotosPreviewViewModel
import com.ilya.theme.LocalTypography
import kotlinx.coroutines.flow.combine

@Composable
internal fun AllPhotosPreview(
    viewModel: PhotosPreviewViewModel,
    onBackClick: () -> Unit,
    userId: Long,
    targetPhotoIndex: Int,
) {
    val photos = viewModel.photosFlow.collectAsLazyPagingItems()
    val systemUiController = rememberSystemUiController()
    val likesState by viewModel.likesState.collectAsState()
    val snackbarState by viewModel.snackbarState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    BackHandler {
        systemUiController.isStatusBarVisible = true
        onBackClick()
    }

    LaunchedEffect(Unit) {
        systemUiController.isStatusBarVisible = false
        viewModel.handleEvent(PhotosPreviewEvent.Start(userId, targetPhotoIndex))
    }

    val pagerState = rememberPagerState(
        initialPage = targetPhotoIndex,
        pageCount = { photos.itemCount }
    )
    val likes = remember { mutableStateOf<Likes?>(null) }
    var currentPhoto by remember { mutableStateOf<Photo?>(null) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            PreviewTopBar(
                systemUiController = systemUiController,
                onBackClick = {
                    systemUiController.isStatusBarVisible = true
                    onBackClick()
                },
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

        LaunchedEffect(pagerState) {
            val snapshotFlow = combine(
                snapshotFlow { pagerState.currentPage },
                snapshotFlow { photos.loadState },
                snapshotFlow { likesState }
            ) { page, _, _ -> page }

            snapshotFlow.collect {
                currentPhoto = photos.getOrNull(it)
                likes.value = currentPhoto?.id?.let { id -> likesState.likes[id] }
            }
        }

        LaunchedEffect(Unit) {
            snapshotFlow { photos.itemSnapshotList.items }.collect { items ->
                val likesList = items.mapNotNull {
                    it.likes?.let { likes ->
                        it.id to likes
                    }
                }.toMap()
                viewModel.handleEvent(PhotosPreviewEvent.PhotosAdded(likesList))
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
private fun PreviewBottomBar(
    likes: MutableState<Likes?>,
    currentPhoto: Photo?,
    onLikeClick: (Photo?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.07f)
            .background(Color.Black)
            .padding(start = 16.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(
            modifier = Modifier.size(32.dp),
            onClick = {
                val oldLikes = likes.value ?: return@IconButton
                likes.value = oldLikes.copy(
                    userLikes = !oldLikes.userLikes,
                    count = if (oldLikes.userLikes) oldLikes.count - 1 else oldLikes.count + 1
                )
                onLikeClick(currentPhoto?.copy(likes = oldLikes))
            }
        ) {
            Icon(
                modifier = Modifier.fillMaxSize(),
                imageVector = if (likes.value?.userLikes == true) {
                    Icons.Default.Favorite
                } else {
                    Icons.Default.FavoriteBorder
                },
                contentDescription = "like",
                tint = likes.value?.let {
                    if (it.userLikes) {
                        Color.Red
                    } else {
                        Color.White
                    }
                } ?: Color.Gray
            )
        }
        likes.value?.let {
            Text(
                text = it.count.toString(),
                color = if (it.userLikes) Color.Red else Color.White,
                fontSize = LocalTypography.current.big
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PreviewTopBar(
    systemUiController: SystemUiController,
    onBackClick: () -> Unit,
    photosCount: Int,
    currentPage: Int
) {
    TopAppBar(
        title = {
            if (photosCount != 0) {
                Text(
                    text = stringResource(R.string.photo_number, currentPage + 1, photosCount),
                    color = Color.White,
                    fontSize = LocalTypography.current.large
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    systemUiController.isStatusBarVisible = true
                    onBackClick()
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "backFromPhoto",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
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
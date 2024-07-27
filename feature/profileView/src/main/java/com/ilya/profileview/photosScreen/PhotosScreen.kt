package com.ilya.profileview.photosScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.ilya.core.appCommon.StringResource
import com.ilya.core.appCommon.enums.PhotoSize
import com.ilya.core.basicComposables.OnError
import com.ilya.paging.PaginationError
import com.ilya.paging.models.Photo
import com.ilya.profileview.R
import com.ilya.profileview.photosScreen.event.PhotosScreenEvent
import com.ilya.profileview.photosScreen.event.PhotosScreenNavEvent
import com.ilya.profileview.profileScreen.ErrorType
import com.ilya.theme.LocalColorScheme
import com.ilya.theme.LocalTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotosScreen(userId: Long, handleNavEvent: (PhotosScreenNavEvent) -> Unit) {
    val viewModel: PhotosScreenViewModel = hiltViewModel()
    val photos = viewModel.photosFlow.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.photos_screen_name)) },
                navigationIcon = {
                    IconButton(onClick = { handleNavEvent(PhotosScreenNavEvent.BackClick) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LocalColorScheme.current.cardContainerColor,
                    titleContentColor = LocalColorScheme.current.primaryTextColor,
                    navigationIconContentColor = LocalColorScheme.current.primaryTextColor
                )
            )
        },
        containerColor = LocalColorScheme.current.background
    ) { padding ->
        Content(padding, photos, handleNavEvent)
    }

    LaunchedEffect(Unit) {
        viewModel.handleEvent(PhotosScreenEvent.Start(userId))
    }

}

@Composable
private fun Content(
    padding: PaddingValues,
    photos: LazyPagingItems<Photo>,
    handleNavEvent: (PhotosScreenNavEvent) -> Unit
) {
    LazyVerticalGrid(
        modifier = Modifier.padding(padding),
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(count = photos.itemCount) { Photo(photos, it, handleNavEvent) }
        item(span = { GridItemSpan(3) }) { ResolveRefresh(photos, handleNavEvent) }
        item(span = { GridItemSpan(3) }) { ResolveAppend(photos, handleNavEvent) }
        item(span = { GridItemSpan(3) }) { PhotosCount(photos) }
    }
}

@Composable
private fun ResolveRefresh(photos: LazyPagingItems<Photo>, handleNavEvent: (PhotosScreenNavEvent) -> Unit) {
    when (val state = photos.loadState.refresh) {
        LoadState.Loading -> OnLoading(modifier = Modifier.height(500.dp))
        is LoadState.Error -> OnPagingError(
            modifier = Modifier.height(500.dp),
            errorType = state.error.correspondingErrorType(),
            onTryAgainClick = { photos.refresh() },
            onEmptyAccessToken = { handleNavEvent(PhotosScreenNavEvent.EmptyAccessToken) }
        )

        is LoadState.NotLoading -> Unit
    }
}

@Composable
private fun ResolveAppend(photos: LazyPagingItems<Photo>, handleNavEvent: (PhotosScreenNavEvent) -> Unit) {
    when (val state = photos.loadState.append) {
        LoadState.Loading -> OnLoading(modifier = Modifier.height(120.dp))
        is LoadState.Error -> {

            OnPagingError(
                modifier = Modifier.height(120.dp),
                errorType = state.error.correspondingErrorType(),
                onTryAgainClick = { photos.retry() },
                onEmptyAccessToken = { handleNavEvent(PhotosScreenNavEvent.EmptyAccessToken) }
            )
        }

        is LoadState.NotLoading -> Unit
    }
}

@Composable
fun OnLoading(modifier: Modifier) {
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = LocalColorScheme.current.primaryIconTintColor)
    }
}

@Composable
private fun OnPagingError(
    modifier: Modifier = Modifier,
    errorType: ErrorType,
    onTryAgainClick: () -> Unit,
    onEmptyAccessToken: () -> Unit,
) {
    when (errorType) {
        ErrorType.NoInternet -> OnError(
            modifier = modifier,
            message = StringResource.FromId(R.string.error_no_internet),
            buttonText = StringResource.FromId(R.string.try_again),
            onButtonClick = onTryAgainClick
        )

        ErrorType.NoAccessToken -> onEmptyAccessToken()
        is ErrorType.Unknown -> OnError(
            modifier = modifier,
            message = StringResource.FromId(
                id = R.string.error_unknown,
                formatArgs = listOf(errorType.error.message.toString())
            ),
            buttonText = StringResource.FromId(id = R.string.try_again),
            onButtonClick = onTryAgainClick
        )
    }
}

@Composable
private fun PhotosCount(photos: LazyPagingItems<Photo>) {
    if (photos.itemCount != 0 && photos.loadState.append is LoadState.NotLoading && photos.loadState.refresh is LoadState.NotLoading) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.photos_count, photos.itemCount),
                color = LocalColorScheme.current.secondaryTextColor,
                fontSize = LocalTypography.current.average
            )
        }
    }
}

@Composable
private fun Photo(
    photos: LazyPagingItems<Photo>,
    index: Int,
    handleNavEvent: (PhotosScreenNavEvent) -> Unit
) {
    val photo = photos[index]
    if (photo != null) {
        AsyncImage(
            modifier = Modifier
                .aspectRatio(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .clickable { handleNavEvent(PhotosScreenNavEvent.OpenPhoto(photo.ownerId, index)) },
            model = photo.sizes.find { it.type == PhotoSize.X }?.url,
            contentDescription = "userPhoto",
            contentScale = ContentScale.Crop
        )
    }
}

private fun Throwable.correspondingErrorType(): ErrorType {
    return when (this) {
        is PaginationError.NoInternet -> ErrorType.NoInternet
        is PaginationError.NoAccessToken -> ErrorType.NoAccessToken
        else -> ErrorType.Unknown(this)
    }
}

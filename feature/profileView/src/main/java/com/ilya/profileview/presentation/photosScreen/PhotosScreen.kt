package com.ilya.profileview.presentation.photosScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.ilya.core.appCommon.enums.PhotoSize
import com.ilya.profileViewDomain.models.Photo
import com.ilya.profileview.R
import com.ilya.theme.LocalColorScheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotosScreen(
    userId: Long,
    viewModel: PhotosScreenViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onPhotoClick: (userId: Long, photoIndex: Int) -> Unit
) {
    val photos = viewModel.photosFlow.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.photos_screen_name)) },
                navigationIcon = {
                    IconButton(onBackClick) {
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
        containerColor = LocalColorScheme.current.cardContainerColor
    ) { padding ->
        Content(padding, photos, onPhotoClick)
    }

    LaunchedEffect(Unit) {
        viewModel.handleEvent(PhotosScreenEvent.Start(userId))
    }

}

@Composable
private fun Content(
    padding: PaddingValues,
    photos: LazyPagingItems<Photo>,
    onPhotoClick: (userId: Long, photoIndex: Int) -> Unit
) {
    LazyVerticalGrid(
        modifier = Modifier.padding(padding),
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(count = photos.itemCount) { index ->
            val photo = photos[index]
            if (photo != null) {
                AsyncImage(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { onPhotoClick(photo.ownerId, index) },
                    model = photo.sizes.find { it.type == PhotoSize.X }?.url,
                    contentDescription = "userPhoto",
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

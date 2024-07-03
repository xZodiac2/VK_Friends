package com.ilya.profileview.profileScreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ilya.core.appCommon.enums.PhotoSize
import com.ilya.profileViewDomain.models.Photo
import com.ilya.profileview.R
import com.ilya.theme.LocalColorScheme
import com.ilya.theme.LocalTypography

@Composable
internal fun Photos(
    photos: List<Photo>,
    onPhotoClick: (userId: Long, targetPhotoIndex: Int) -> Unit,
    onOpenPhotosClick: () -> Unit
) {
    if (photos.isNotEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = LocalColorScheme.current.cardContainerColor),
            elevation = CardDefaults.cardElevation(8.dp),
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(LocalColorScheme.current.primary)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    PhotosGrid(photos, onPhotoClick)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(onOpenPhotosClick) {
                        Text(
                            text = stringResource(R.string.open_photos),
                            color = LocalColorScheme.current.primaryTextColor,
                            fontSize = LocalTypography.current.average
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "seeAll",
                            tint = LocalColorScheme.current.primaryTextColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PhotosGrid(
    photos: List<Photo>,
    onPhotoClick: (userId: Long, targetPhotoIndex: Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        photos.chunked(3).forEach {
            Row(modifier = Modifier.fillMaxWidth((1f / 3f) * it.size)) {
                it.forEach { photo ->
                    val index = photos.indexOf(photo)
                    AsyncImage(
                        modifier = Modifier
                            .weight(1f)
                            .padding(2.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .aspectRatio(1f)
                            .clickable { onPhotoClick(photo.ownerId, index) },
                        model = photo.sizes.find { it.type == PhotoSize.X }?.url,
                        contentDescription = "",
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

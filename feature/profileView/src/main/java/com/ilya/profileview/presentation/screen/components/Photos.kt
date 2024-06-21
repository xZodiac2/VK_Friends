package com.ilya.profileview.presentation.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ilya.core.appCommon.enums.PhotoSize
import com.ilya.profileViewDomain.Photo
import com.ilya.theme.LocalColorScheme

@Composable
internal fun Photos(photos: List<Photo>) {
    if (photos.isNotEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = LocalColorScheme.current.cardContainerColor),
            elevation = CardDefaults.cardElevation(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(LocalColorScheme.current.primary)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    photos.chunked(3).forEach {
                        Row(modifier = Modifier.fillMaxWidth((1f / 3f) * photos.size)) {
                            it.forEach { photo ->
                                AsyncImage(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(2.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .aspectRatio(1f),
                                    model = photo.sizes.find { it.type == PhotoSize.X }?.url,
                                    contentDescription = "",
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
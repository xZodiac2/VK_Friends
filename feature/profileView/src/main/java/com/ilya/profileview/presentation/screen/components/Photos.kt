package com.ilya.profileview.presentation.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
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
fun Photos(photos: List<Photo>) {
    if (photos.isNotEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = LocalColorScheme.current.cardContainerColor),
            elevation = CardDefaults.cardElevation(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(LocalColorScheme.current.primary)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                val rowHeight = 135.dp

                LazyHorizontalGrid(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .height(rowHeight * photos.chunked(3).size)
                        .fillMaxWidth(),
                    rows = GridCells.Fixed(photos.chunked(3).size),
                    contentPadding = PaddingValues(12.dp),
                    userScrollEnabled = false,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(photos.chunked(3)) { photoList ->
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            photoList.forEach { photo ->
                                AsyncImage(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .size(120.dp),
                                    model = photo.sizes.find { it.type == PhotoSize.X }?.url,
                                    contentDescription = "user_photo",
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
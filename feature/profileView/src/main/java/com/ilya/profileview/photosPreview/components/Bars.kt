package com.ilya.profileview.photosPreview.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ilya.profileViewDomain.models.Likes
import com.ilya.profileViewDomain.models.Photo
import com.ilya.profileview.R
import com.ilya.theme.LocalTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewTopBar(
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
            IconButton(onBackClick) {
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
fun PreviewBottomBar(
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
                likes.value?.let {
                    likes.value = it.copy(
                        userLikes = !it.userLikes,
                        count = if (it.userLikes) it.count - 1 else it.count + 1
                    )
                    onLikeClick(currentPhoto?.copy(likes = it))
                }
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
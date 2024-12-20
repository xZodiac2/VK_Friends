package com.ilya.profileview.videoPreview

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.ilya.core.appCommon.compose.basicComposables.snackbar.SnackbarEventEffect
import com.ilya.paging.models.Likes
import com.ilya.paging.models.VideoExtended
import com.ilya.theme.LocalTypography

@Composable
fun VideoPreview(
    ownerId: Long,
    videoId: Long,
    accessKey: String,
    onBackClick: () -> Unit
) {
    val viewModel: VideoPreviewViewModel = hiltViewModel()

    val video by viewModel.videoState.collectAsState()
    val likes by viewModel.likesState.collectAsState()
    val snackbarState by viewModel.snackbarState.collectAsState()

    val snackbarHost = remember { SnackbarHostState() }

    BackHandler {
        onBackClick()
    }

    SnackbarEventEffect(
        state = snackbarState,
        onConsumed = { viewModel.handleEvent(VideoPreviewEvent.SnackbarConsumed) },
        action = { snackbarHost.showSnackbar(it) }
    )

    Scaffold(
        topBar = {
            VideoPreviewTopBar(
                videoTitle = video?.title ?: "",
                onBackClick = {
                    onBackClick()
                }
            )
        },
        bottomBar = {
            VideoPreviewLikes(
                likes = likes,
                onLikeClick = { viewModel.handleEvent(VideoPreviewEvent.Like(video)) }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHost) },
        containerColor = Color.Black
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .background(Color.Black)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            video?.let {
                Content(it)
            } ?: CircularProgressIndicator(color = Color.White)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.handleEvent(VideoPreviewEvent.Start(ownerId, videoId, accessKey))
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VideoPreviewTopBar(videoTitle: String, onBackClick: () -> Unit) {
    TopAppBar(
        title = {
            val fontSize = when {
                videoTitle.length < 20 -> LocalTypography.current.big
                videoTitle.length in 20..35 -> LocalTypography.current.average
                else -> LocalTypography.current.small
            }
            Text(
                text = videoTitle,
                fontSize = fontSize,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        navigationIcon = {
            IconButton(onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "back",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
    )
}

@Composable
private fun VideoPreviewLikes(likes: Likes?, onLikeClick: () -> Unit) {
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
            onClick = onLikeClick
        ) {
            Icon(
                modifier = Modifier.fillMaxSize(),
                imageVector = if (likes?.userLikes == true) {
                    Icons.Default.Favorite
                } else {
                    Icons.Default.FavoriteBorder
                },
                contentDescription = "like",
                tint = likes?.let {
                    if (it.userLikes) {
                        Color.Red
                    } else {
                        Color.White
                    }
                } ?: Color.Gray
            )
        }
        likes?.let {
            Text(
                text = it.count.toString(),
                color = if (it.userLikes) Color.Red else Color.White,
                fontSize = LocalTypography.current.big
            )
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun Content(video: VideoExtended) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                settings.javaScriptEnabled = true
                webViewClient = WebViewClient()
                loadUrl(video.playerUrl)
            }
        },
        update = {
            it.loadUrl(video.playerUrl)
        }
    )
}

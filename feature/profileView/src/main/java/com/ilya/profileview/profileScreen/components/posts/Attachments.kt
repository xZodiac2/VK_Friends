package com.ilya.profileview.profileScreen.components.posts

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.ilya.core.appCommon.compose.ImmutablePair
import com.ilya.core.appCommon.enums.PhotoSize
import com.ilya.paging.models.Audio
import com.ilya.paging.models.Photo
import com.ilya.paging.models.Video
import com.ilya.profileview.R
import com.ilya.profileview.profileScreen.screens.event.receiver.ProfileScreenEventReceiver
import com.ilya.theme.LocalColorScheme
import com.ilya.theme.LocalTypography

@Composable
internal fun Attachments(
    attachments: List<Any>,
    currentLoopingAudio: State<ImmutablePair<Audio?, Boolean>>,
    eventReceiver: ProfileScreenEventReceiver
) {
    val photos = remember(attachments) { attachments.mapNotNull { it as? Photo } }
    val videos = remember(attachments) { attachments.mapNotNull { it as? Video } }
    val audios = remember(attachments) { attachments.mapNotNull { it as? Audio } }
    val photosAndVideos = remember(photos, videos) { photos + videos }

    if (photosAndVideos.isNotEmpty()) {
        if (photosAndVideos.size > 6) {
            AttachmentsSlider(photosAndVideos, eventReceiver)
        } else {
            AttachmentsGrid(photosAndVideos, eventReceiver)
        }

    }

    if (audios.isNotEmpty()) {
        for (audio in audios) {
            Audio(
                audio = audio,
                onAudioClick = { eventReceiver.onAudioClick(audio) },
                currentLoopingAudio = currentLoopingAudio
            )
        }
    }
}

@Composable
private fun AttachmentsGrid(photosAndVideos: List<Any>, eventReceiver: ProfileScreenEventReceiver) {
    val photos = remember(photosAndVideos) { photosAndVideos.mapNotNull { it as? Photo } }

    Column(
        modifier = Modifier
            .background(Color.Black)
            .fillMaxWidth()
            .padding(if (photosAndVideos.size == 1) 0.dp else 2.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        val columns = when {
            photosAndVideos.size < 4 -> 2
            photosAndVideos.size in 4..6 -> 3
            else -> throw IllegalArgumentException("Use AttachmentsSlider instead of AttachmentsGrid")
        }

        for (row in photosAndVideos.chunked(columns)) {
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                val modifier = when (row.size) {
                    1 -> Modifier.fillMaxWidth()
                    else -> Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(4.dp))
                }
                val contentScale = when (row.size) {
                    1 -> ContentScale.Fit
                    else -> ContentScale.Crop
                }

                for (attachment in row) {
                    when (attachment) {
                        is Photo -> Photo(
                            modifier = modifier,
                            photo = attachment,
                            onClick = {
                                eventReceiver.onPostPhotoClick(
                                    attachment.ownerId,
                                    photos.indexOf(attachment),
                                    photos.associate { it.id to it.accessKey }
                                )
                            },
                            contentScale = contentScale
                        )

                        is Video -> Video(
                            video = attachment,
                            onVideoClick = { eventReceiver.onVideoClick(attachment) },
                            modifier = modifier,
                            contentScale = contentScale
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AttachmentsSlider(photosAndVideos: List<Any>, eventReceiver: ProfileScreenEventReceiver) {
    val pagerState = rememberPagerState { photosAndVideos.size }
    val photos = remember(photosAndVideos) { photosAndVideos.mapNotNull { it as? Photo } }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.8f)
        ) {
            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(8.dp)
            ) { index ->
                when (val attachment = photosAndVideos[index]) {
                    is Photo -> SubcomposeAsyncImage(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                eventReceiver.onPostPhotoClick(
                                    userId = attachment.ownerId,
                                    targetPhotoIndex = photos.indexOf(attachment),
                                    photoIds = photos.associate { it.id to it.accessKey }
                                )
                            },
                        contentScale = ContentScale.Crop,
                        model = attachment.sizes.find { it.type == PhotoSize.X }?.url,
                        loading = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black),
                                contentAlignment = Alignment.Center
                            ) { CircularProgressIndicator(color = Color.White) }
                        },
                        contentDescription = "photo"
                    )

                    is Video -> Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 0.dp, max = 500.dp)
                            .background(Color.Black)
                            .clickable { eventReceiver.onVideoClick(attachment) },
                        contentAlignment = Alignment.Center
                    ) {
                        SubcomposeAsyncImage(
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .fillMaxSize()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { eventReceiver.onVideoClick(attachment) },
                            contentScale = ContentScale.Crop,
                            model = attachment.firstFrame.lastOrNull()?.url,
                            loading = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black),
                                    contentAlignment = Alignment.Center
                                ) { CircularProgressIndicator(color = Color.White) }
                            },
                            contentDescription = "video"
                        )
                        Icon(
                            modifier = Modifier.fillMaxSize(0.2f),
                            painter = painterResource(R.drawable.play),
                            contentDescription = "play",
                            tint = Color(255, 255, 255, 138),
                        )
                    }
                }
            }
        }
        PagerIndication(pagerState)
    }
}

@Composable
private fun PagerIndication(pagerState: PagerState) {
    Row(
        modifier = Modifier.height(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(pagerState.pageCount) { page ->
            Indicator(pagerState.currentPage, page)
        }
    }
}

@Composable
private fun Indicator(currentPage: Int, pageNumber: Int) {
    val background = with(LocalColorScheme.current) {
        animateColorAsState(
            targetValue = if (currentPage == pageNumber) primaryIconTintColor else faded,
            label = "pagerIndicatorsBackground"
        )
    }
    val size = animateDpAsState(
        targetValue = if (currentPage == pageNumber) 12.dp else 8.dp,
        label = "pagerIndicatorsBackground"
    )
    Box(
        modifier = Modifier
            .size(size.value)
            .clip(CircleShape)
            .background(background.value)
    )
}

@Composable
private fun RowScope.Photo(
    photo: Photo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentScale: ContentScale
) {
    AsyncImage(
        modifier = Modifier
            .heightIn(max = 500.dp)
            .weight(1f)
            .background(Color.Black)
            .then(modifier)
            .clickable { onClick() },
        contentScale = contentScale,
        model = photo.sizes.find { it.type == PhotoSize.X }?.url,
        contentDescription = "Photo"
    )
}

@Composable
private fun RowScope.Video(
    video: Video,
    onVideoClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentScale: ContentScale
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 0.dp, max = 500.dp)
            .background(Color.Black)
            .weight(1f)
            .clickable { onVideoClick() },
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            modifier = modifier,
            contentScale = contentScale,
            model = video.firstFrame.lastOrNull()?.url,
            contentDescription = "video"
        )
        Icon(
            modifier = Modifier.fillMaxSize(0.2f),
            painter = painterResource(R.drawable.play),
            contentDescription = "play",
            tint = Color(255, 255, 255, 138),
        )
    }
}

@Composable
private fun Audio(
    audio: Audio,
    onAudioClick: () -> Unit,
    currentLoopingAudio: State<ImmutablePair<Audio?, Boolean>>
) {
    val color = when {
        audio.url.isBlank() -> Color.Gray
        else -> LocalColorScheme.current.primaryTextColor
    }

    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .height(50.dp)
            .clickable { onAudioClick() },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .fillMaxHeight()
                    .aspectRatio(1f)
                    .background(LocalColorScheme.current.background),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.fillMaxSize(0.5f),
                    painter = painterResource(R.drawable.music),
                    contentDescription = "music",
                    tint = color
                )
            }
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(0.7f),
                    text = audio.title,
                    fontSize = LocalTypography.current.average,
                    color = color,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    modifier = Modifier.fillMaxWidth(0.7f),
                    text = audio.artist,
                    color = color,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = onAudioClick,
                modifier = Modifier
                    .clip(CircleShape)
                    .aspectRatio(1f),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = LocalColorScheme.current.background),
                contentPadding = PaddingValues(0.dp)
            ) {
                val isPlaying = currentLoopingAudio.value.second && currentLoopingAudio.value.first == audio

                val iconId = if (isPlaying) R.drawable.stop else R.drawable.play
                Icon(
                    modifier = Modifier
                        .fillMaxSize(0.3f)
                        .align(Alignment.CenterVertically),
                    painter = painterResource(iconId),
                    tint = color,
                    contentDescription = "playMusic"
                )
            }
        }
    }
}

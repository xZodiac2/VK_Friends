package com.ilya.profileview.profileScreen.components.posts

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ilya.core.appCommon.enums.PhotoSize
import com.ilya.paging.Attachment
import com.ilya.paging.Audio
import com.ilya.paging.Photo
import com.ilya.paging.Video
import com.ilya.profileview.R
import com.ilya.profileview.profileScreen.screens.event.EventReceiver
import com.ilya.theme.LocalColorScheme
import com.ilya.theme.LocalTypography

@Composable
internal fun Attachments(
    attachments: List<Attachment>,
    currentLoopingAudio: Pair<Audio?, Boolean>,
    eventReceiver: EventReceiver
) {
    val photos = attachments.mapNotNull { it as? Photo }
    val videos = attachments.mapNotNull { it as? Video }
    val audios = attachments.mapNotNull { it as? Audio }
    val photosAndVideos = photos + videos

    if (photosAndVideos.isNotEmpty()) {
        Column(
            modifier = Modifier
                .background(Color.Black)
                .fillMaxWidth()
                .padding(if (photosAndVideos.size == 1) 0.dp else 2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            val columns = when {
                photosAndVideos.size < 4 -> 2
                photosAndVideos.size in 4..6 -> 4
                photosAndVideos.size in 7..10 -> 6
                else -> 8
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

    if (audios.isNotEmpty()) {
        for (audio in audios) {
            Audio(
                audio = audio,
                onAudioClick = { eventReceiver.onAudioClick(audio) },
                isPlaying = currentLoopingAudio.second && currentLoopingAudio.first == audio
            )
        }
    }
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
            contentDescription = "Photo"
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
    isPlaying: Boolean,
    onAudioClick: () -> Unit
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
                val iconId = if (isPlaying) R.drawable.stop else R.drawable.play
                Icon(
                    modifier = Modifier
                        .fillMaxSize(0.5f)
                        .align(Alignment.CenterVertically),
                    painter = painterResource(iconId),
                    tint = color,
                    contentDescription = "playMusic"
                )
            }
        }
    }
}

package com.ilya.profileview.profileScreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ilya.core.appCommon.enums.PhotoSize
import com.ilya.profileViewDomain.models.Attachment
import com.ilya.profileViewDomain.models.Audio
import com.ilya.profileViewDomain.models.Photo
import com.ilya.profileViewDomain.models.VideoExtended
import com.ilya.profileview.R
import com.ilya.theme.LocalColorScheme

@Composable
internal fun Attachments(
    photos: List<Photo>,
    videos: List<VideoExtended>,
    audios: List<Audio>,
    onPhotoClick: (ownerId: Long, targetPhotoIndex: Int, photoUrls: Map<Long, String>) -> Unit
) {
    val photosAndVideos: List<Attachment> = photos + videos

    if ((photosAndVideos + audios).isNotEmpty()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            val columns = when {
                photosAndVideos.size < 4 -> 2
                photosAndVideos.size in 4..6 -> 4
                photosAndVideos.size in 6..10 -> 6
                else -> 8
            }

            for (row in photosAndVideos.chunked(columns)) {
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    val modifier = if (row.size == 1) {
                        Modifier
                    } else {
                        Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(2.dp))
                    }

                    for (attachment in row) {
                        when (attachment) {
                            is Photo -> Photo(
                                modifier = modifier,
                                photo = attachment,
                                onClick = {
                                    onPhotoClick(
                                        attachment.ownerId,
                                        photos.indexOf(attachment),
                                        photos.associate { it.id to it.accessKey }
                                    )
                                }
                            )

                            is VideoExtended -> Video(attachment, modifier)
                        }
                    }
                }
            }
            for (audio in audios) {
                Audio()
            }
        }
    }
}

@Composable
private fun RowScope.Photo(
    photo: Photo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        modifier = Modifier
            .fillMaxSize()
            .weight(1f)
            .then(modifier)
            .clickable { onClick() },
        contentScale = ContentScale.Crop,
        model = photo.sizes.find { it.type == PhotoSize.X }?.url,
        contentDescription = "Photo"
    )
}

@Composable
private fun RowScope.Video(video: VideoExtended, modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .weight(1f)
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            modifier = modifier
                .fillMaxSize()
                .then(modifier),
            contentScale = ContentScale.Crop,
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
private fun Audio() {
    Box(
        modifier = Modifier
            .background(Color.Black)
            .fillMaxWidth()
            .height(100.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("Audio", color = LocalColorScheme.current.primaryTextColor)
    }
}

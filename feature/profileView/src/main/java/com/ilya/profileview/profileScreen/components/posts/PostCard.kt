package com.ilya.profileview.profileScreen.components.posts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import com.ilya.profileViewDomain.models.Audio
import com.ilya.profileViewDomain.models.Likes
import com.ilya.profileViewDomain.models.Post
import com.ilya.profileViewDomain.models.PostAuthor
import com.ilya.profileViewDomain.models.RepostedPost
import com.ilya.profileViewDomain.models.Video
import com.ilya.profileview.R
import com.ilya.theme.LocalColorScheme
import com.ilya.theme.LocalTypography

@Composable
internal fun PostCard(
    post: Post,
    likes: Likes?,
    currentLoopingAudio: Pair<Audio?, Boolean>,
    onLikeClick: (Post) -> Unit,
    onPhotoClick: (ownerId: Long, targetPhotoIndex: Int, photoIds: Map<Long, String>) -> Unit,
    onAudioClick: (Audio) -> Unit,
    onVideoClick: (Video) -> Unit,
) {
    Box(contentAlignment = Alignment.Center) {
        Card(
            modifier = Modifier.fillMaxSize(),
            colors = CardDefaults.cardColors(
                containerColor = LocalColorScheme.current.cardContainerColor,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
        ) {
            val attachments = post.photos + post.audios + post.videos

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Author(post.author, post.date)
                PostText(post, attachments.isEmpty())
                Attachments(attachments, onPhotoClick, onAudioClick, currentLoopingAudio, onVideoClick)
                OptionalRepostedPost(post, onPhotoClick, onAudioClick, currentLoopingAudio, onVideoClick)
                Likes(
                    likes = likes,
                    onLikeClick = { onLikeClick(post.copy(likes = it)) }
                )
            }
        }
    }
}

@Composable
private fun PostText(post: Post, isAttachmentsEmpty: Boolean) {
    val fontSize = when {
        post.text.length < 60 && isAttachmentsEmpty -> LocalTypography.current.large
        post.text.length in 60..120 && isAttachmentsEmpty -> LocalTypography.current.big
        else -> LocalTypography.current.average
    }

    if (post.text.isNotBlank()) {
        Text(
            modifier = Modifier.padding(horizontal = 20.dp),
            text = post.text,
            color = LocalColorScheme.current.primaryTextColor,
            fontSize = fontSize
        )
    }
}

@Composable
private fun OptionalRepostedPost(
    post: Post,
    onPhotoClick: (ownerId: Long, targetPhotoIndex: Int, photoIds: Map<Long, String>) -> Unit,
    onAudioClick: (Audio) -> Unit,
    currentLoopingAudio: Pair<Audio?, Boolean>,
    onVideoClick: (Video) -> Unit,
) {
    post.reposted?.let {
        val attachments = post.photos + post.videos + post.audios

        if (post.text.isNotBlank() || attachments.isNotEmpty()) {
            HorizontalDivider(color = LocalColorScheme.current.secondaryTextColor)
        }
        RepostedAuthor(it)
        RepostedText(it, attachments.isEmpty())
        Attachments(
            attachments = it.photos + it.videos + it.audios,
            onPhotoClick = onPhotoClick,
            onAudioClick = onAudioClick,
            currentLoopingAudio = currentLoopingAudio,
            onVideoClick = onVideoClick
        )
    }
}

@Composable
private fun RepostedText(post: RepostedPost, isAttachmentsEmpty: Boolean) {
    if (post.text.isNotBlank()) {
        val repostedFontSize = when {
            post.text.length < 60 && isAttachmentsEmpty -> LocalTypography.current.large
            post.text.length in 60..120 && isAttachmentsEmpty -> LocalTypography.current.big
            else -> LocalTypography.current.average
        }
        Text(
            modifier = Modifier.padding(horizontal = 20.dp),
            text = post.text,
            color = LocalColorScheme.current.primaryTextColor,
            fontSize = repostedFontSize
        )
    }
}

@Composable
private fun Author(author: PostAuthor, date: String) {
    Row(
        modifier = Modifier.padding(top = 12.dp, start = 20.dp, end = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            model = author.photoUrl,
            contentDescription = "postOwnerAvatar",
            contentScale = ContentScale.Crop
        )
        Column {
            Text(
                text = "${author.firstName} ${author.lastName}",
                fontSize = LocalTypography.current.big,
                color = LocalColorScheme.current.primaryTextColor
            )
            Text(
                text = date,
                color = LocalColorScheme.current.secondaryTextColor
            )
        }
    }
}

@Composable
private fun Likes(
    likes: Likes?,
    onLikeClick: (Likes) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(bottom = 8.dp, start = 12.dp)
            .clickable { likes?.let(onLikeClick) }
            .clip(RoundedCornerShape(20.dp))
            .background(LocalColorScheme.current.background)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            likes?.let {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = if (it.userLikes) {
                        Icons.Default.Favorite
                    } else {
                        Icons.Default.FavoriteBorder
                    },
                    tint = if (it.userLikes) {
                        Color.Red
                    } else {
                        LocalColorScheme.current.primaryTextColor
                    },
                    contentDescription = "postLikes"
                )
                Text(
                    text = it.count.toString(),
                    color = if (it.userLikes) {
                        Color.Red
                    } else {
                        LocalColorScheme.current.primaryTextColor
                    },
                    fontSize = LocalTypography.current.tiny
                )
            }
        }
    }
}

@Composable
private fun RepostedAuthor(reposted: RepostedPost) {
    Row(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(R.drawable.repost),
            contentDescription = "repost",
            tint = LocalColorScheme.current.primaryTextColor
        )
        if (reposted.repostedByGroup) {
            reposted.group?.let {
                AsyncImage(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape),
                    model = it.photoUrl,
                    contentDescription = "ownerPhoto",
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = it.name,
                    color = LocalColorScheme.current.primaryTextColor
                )
            }
        } else {
            reposted.owner?.let {
                AsyncImage(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape),
                    model = it.photoUrl,
                    contentDescription = "ownerPhoto",
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = "${it.firstName} ${it.lastName}",
                    color = LocalColorScheme.current.primaryTextColor
                )
            }
        }
    }
}
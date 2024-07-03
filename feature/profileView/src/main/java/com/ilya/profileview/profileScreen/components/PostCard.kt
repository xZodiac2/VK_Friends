package com.ilya.profileview.profileScreen.components

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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ilya.profileViewDomain.models.Likes
import com.ilya.profileViewDomain.models.Post
import com.ilya.profileViewDomain.models.PostAuthor
import com.ilya.profileViewDomain.models.RepostedPost
import com.ilya.profileview.R
import com.ilya.theme.LocalColorScheme
import com.ilya.theme.LocalTypography

@Composable
internal fun PostCard(
    post: Post,
    onLikeClick: (Post) -> Unit,
    likes: Likes?,
    onPhotoClick: (ownerId: Long, targetPhotoIndex: Int, photoIds: Map<Long, String>) -> Unit
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
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Author(post.author, post.date)
                if (post.text.isNotBlank()) {
                    val fontSize = when {
                        post.text.length < 40 -> LocalTypography.current.large
                        post.text.length in 40..100 -> LocalTypography.current.big
                        else -> LocalTypography.current.average
                    }

                    Text(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        text = post.text,
                        color = LocalColorScheme.current.primaryTextColor,
                        fontSize = fontSize
                    )
                }
                Attachments(
                    photos = post.photos,
                    videos = post.videos,
                    audios = post.audios,
                    onPhotoClick = onPhotoClick
                )
                post.reposted?.let {
                    if (post.text.isNotBlank() || with(post) { audios + videos + photos }.isNotEmpty()) {
                        HorizontalDivider(color = LocalColorScheme.current.secondaryTextColor)
                    }
                    RepostedAuthor(it)
                    if (it.text.isNotBlank()) {
                        Text(
                            modifier = Modifier.padding(start = 12.dp, end = 12.dp),
                            text = it.text,
                            color = LocalColorScheme.current.primaryTextColor,
                            fontSize = LocalTypography.current.average
                        )
                    }
                    Attachments(it.photos, it.videos, it.audios, onPhotoClick)
                }
                Likes(
                    likes = likes,
                    onLikeClick = { onLikeClick(post.copy(likes = it)) }
                )
            }
        }
    }
}

@Composable
private fun Author(author: PostAuthor, date: String) {
    Row(
        modifier = Modifier.padding(top = 12.dp, start = 12.dp, end = 12.dp),
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
                fontSize = LocalTypography.current.big
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
    TextButton(
        onClick = { likes?.let(onLikeClick) },
        enabled = likes != null
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            likes?.let {
                Icon(
                    modifier = Modifier.size(32.dp),
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
                    fontSize = LocalTypography.current.big
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
            contentDescription = "repost"
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

package com.ilya.profileview.profileScreen.components.posts

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ilya.paging.models.Audio
import com.ilya.paging.models.CommentsInfo
import com.ilya.paging.models.Likes
import com.ilya.paging.models.Post
import com.ilya.paging.models.PostAuthor
import com.ilya.paging.models.RepostedPost
import com.ilya.profileview.R
import com.ilya.profileview.profileScreen.PostsLikesState
import com.ilya.profileview.profileScreen.screens.event.EventReceiver
import com.ilya.theme.LocalColorScheme
import com.ilya.theme.LocalTypography

@Composable
internal fun PostCard(
    post: Post,
    likes: State<PostsLikesState>,
    currentLoopingAudio: State<Pair<Audio?, Boolean>>,
    eventReceiver: EventReceiver
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
            val attachments = remember { post.photos + post.audios + post.videos }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Author(post.author, post.date, eventReceiver)
                PostText(post, attachments.isEmpty())
                Attachments(attachments, currentLoopingAudio, eventReceiver)
                OptionalRepostedPost(post, currentLoopingAudio, eventReceiver)
                BottomPostRow(
                    likes = likes,
                    postId = post.id,
                    commentsInfo = post.commentsInfo,
                    onLikeClick = { eventReceiver.onLike(post.copy(likes = it)) },
                    onCommentsClick = { eventReceiver.onCommentsClick(post.id) }
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
    currentLoopingAudio: State<Pair<Audio?, Boolean>>,
    eventReceiver: EventReceiver
) {
    post.reposted?.let {
        val attachments = post.photos + post.videos + post.audios
        RepostedAuthor(it)
        RepostedText(it, attachments.isEmpty())
        Attachments(
            attachments = it.photos + it.videos + it.audios,
            currentLoopingAudio = currentLoopingAudio,
            eventReceiver = eventReceiver
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
private fun Author(author: PostAuthor, date: String, eventReceiver: EventReceiver) {
    Row(
        modifier = Modifier
            .padding(top = 12.dp, start = 20.dp, end = 20.dp)
            .clickable { eventReceiver.onAnotherProfileClick(author.id, author.isPrivate) },
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
private fun BottomPostRow(
    likes: State<PostsLikesState>,
    postId: Long,
    commentsInfo: CommentsInfo,
    onLikeClick: (Likes) -> Unit,
    onCommentsClick: () -> Unit
) {
    Row(
        modifier = Modifier.padding(start = 12.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Likes(likes, onLikeClick, postId)
        Comments(commentsInfo, onCommentsClick)
    }
}

@Composable
private fun Comments(commentsInfo: CommentsInfo, onCommentsClick: () -> Unit) {
    if (commentsInfo.canView) {
        Box(
            modifier = Modifier
                .padding()
                .clickable { onCommentsClick() }
                .clip(RoundedCornerShape(20.dp))
                .background(LocalColorScheme.current.background)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .animateContentSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(R.drawable.comment),
                    tint = LocalColorScheme.current.secondaryTextColor,
                    contentDescription = "postComments"
                )
                if (commentsInfo.count > 0) {
                    Text(
                        text = commentsInfo.count.coerceAtLeast(0).toString(),
                        color = LocalColorScheme.current.secondaryTextColor,
                        fontSize = LocalTypography.current.average
                    )
                }
            }
        }
    }
}

@Composable
private fun Likes(likesState: State<PostsLikesState>, onLikeClick: (Likes) -> Unit, postId: Long) {
    val likes = rememberUpdatedState(likesState.value.likes[postId])

    Box(
        modifier = Modifier
            .clickable { likes.value?.let(onLikeClick) }
            .clip(RoundedCornerShape(20.dp))
            .background(LocalColorScheme.current.background)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .animateContentSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = if (likes.value?.userLikes == true) {
                    Icons.Default.Favorite
                } else {
                    Icons.Default.FavoriteBorder
                },
                tint = if (likes.value?.userLikes == true) {
                    Color.Red
                } else {
                    LocalColorScheme.current.secondaryTextColor
                },
                contentDescription = "postLikes"
            )
            likes.value?.let { likesValue ->
                if (likesValue.count > 0) {
                    AnimatedContent(
                        targetState = likesValue.count,
                        label = "animatedLikesCount",
                        transitionSpec = {
                            slideInVertically {
                                if (likesValue.userLikes) -it else it
                            } togetherWith slideOutVertically {
                                if (likesValue.userLikes) it else -it
                            }
                        }
                    ) { targetState ->
                        Text(
                            text = targetState.coerceAtLeast(0).toString(),
                            color = if (likesValue.userLikes) {
                                Color.Red
                            } else {
                                LocalColorScheme.current.secondaryTextColor
                            },
                            fontSize = LocalTypography.current.average
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RepostedAuthor(reposted: RepostedPost) {
    Row(
        modifier = Modifier.padding(horizontal = 36.dp),
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

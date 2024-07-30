package com.ilya.profileview.profileScreen.components.posts

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ilya.core.appCommon.base.rememberSwitch
import com.ilya.paging.models.Comment
import com.ilya.paging.models.Likeable
import com.ilya.paging.models.Likes
import com.ilya.paging.models.ThreadComment
import com.ilya.profileview.R
import com.ilya.profileview.profileScreen.CommentsBottomSheetState
import com.ilya.profileview.profileScreen.components.profileCommon.CommentsTopBar
import com.ilya.profileview.profileScreen.components.profileCommon.shared.ResolveAppend
import com.ilya.profileview.profileScreen.components.profileCommon.shared.ResolveRefresh
import com.ilya.profileview.profileScreen.screens.event.EventReceiver
import com.ilya.theme.LocalColorScheme
import com.ilya.theme.LocalTypography
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CommentsBottomSheet(stateFlow: StateFlow<CommentsBottomSheetState>, eventReceiver: EventReceiver) {
    val commentsSheetState = stateFlow.collectAsState()
    val sheetState = rememberModalBottomSheetState()

    if (commentsSheetState.value.showSheet) {
        val shape = animateDpAsState(
            targetValue = if (sheetState.targetValue == SheetValue.Expanded) 0.dp else 30.dp,
            label = "sheetCorners",
            animationSpec = tween(200)
        )

        ModalBottomSheet(
            modifier = Modifier.fillMaxSize(),
            onDismissRequest = { eventReceiver.onDismissCommentsSheet() },
            sheetState = sheetState,
            shape = RoundedCornerShape(shape.value),
            containerColor = LocalColorScheme.current.cardContainerColor,
            dragHandle = { BottomSheetDefaults.DragHandle(color = LocalColorScheme.current.primaryTextColor) }
        ) {
            CommentsSheetContent(commentsSheetState.value, eventReceiver)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CommentsSheetContent(commentsSheetState: CommentsBottomSheetState, eventReceiver: EventReceiver) {
    val commentsFlow = rememberUpdatedState(commentsSheetState.commentsFlow)
    val comments = commentsFlow.value.collectAsLazyPagingItems()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            val contentScrolled by remember { derivedStateOf { scrollBehavior.state.contentOffset < -50 } }
            CommentsTopBar(contentScrolled)
        },
        containerColor = LocalColorScheme.current.cardContainerColor
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            items(comments.itemCount, key = comments.itemKey { it.id }) { index ->
                Comment(
                    comments = comments,
                    index = index,
                    commentsSheetState = commentsSheetState,
                    onLikeClick = { eventReceiver.onLike(it) }
                )
            }
            item { ResolveRefresh(comments, eventReceiver) }
            item { ResolveAppend(comments, eventReceiver) }
            item { OnEmptyCommentsMessage(comments) }
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { comments.itemSnapshotList.items }.collect { items ->
            val newLikes = items.mapNotNull {
                it.likes?.let { likes -> it.id to likes }
            }.toMap()
            val newThreadLikes = items.flatMap {
                it.thread.mapNotNull { threadComment ->
                    threadComment.likes?.let { likes -> threadComment.id to likes }
                }
            }.toMap()

            eventReceiver.onCommentsAdded(newLikes + newThreadLikes)
        }
    }
}

@Composable
private fun Comment(
    comments: LazyPagingItems<Comment>,
    index: Int,
    commentsSheetState: CommentsBottomSheetState,
    onLikeClick: (Likeable) -> Unit
) {
    val comment = comments[index]

    if (comment != null) {
        val isCommentThreadExpanded = remember { mutableStateOf(false) }

        CommentBody(comment, isCommentThreadExpanded, commentsSheetState, onLikeClick)
        if (comment.thread.isNotEmpty()) {
            ThreadComments(comment.thread, isCommentThreadExpanded, commentsSheetState, onLikeClick)
        }
    }
}

@Composable
private fun CommentBody(
    comment: Comment,
    isThreadExpanded: MutableState<Boolean>,
    commentsSheetState: CommentsBottomSheetState,
    onLikeClick: (Comment) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AsyncImage(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            model = ImageRequest.Builder(LocalContext.current)
                .fallback(R.drawable.avatar)
                .data(comment.owner?.photoUrl)
                .build(),
            contentScale = ContentScale.Crop,
            contentDescription = "commentOwnerAvatar",
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Column {
                if (comment.isDeleted) {
                    Text(
                        text = stringResource(R.string.comment_deleted),
                        color = LocalColorScheme.current.secondaryTextColor,
                        fontSize = LocalTypography.current.small
                    )
                } else {
                    Text(
                        text = comment.owner?.let { "${it.firstName} ${it.lastName}" }
                            ?: stringResource(R.string.user_not_supports),
                        color = LocalColorScheme.current.secondaryTextColor,
                        fontSize = LocalTypography.current.tiny
                    )
                    Text(
                        text = comment.text.ifEmpty { stringResource(R.string.comment_is_not_supports) },
                        color = if (comment.text.isEmpty()) {
                            LocalColorScheme.current.secondaryTextColor
                        } else {
                            LocalColorScheme.current.primaryTextColor
                        },
                        fontSize = LocalTypography.current.small
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = comment.date,
                        color = LocalColorScheme.current.secondaryTextColor,
                        fontSize = LocalTypography.current.tiny
                    )
                    if (comment.thread.isNotEmpty()) {
                        ExpandThreadButton(isThreadExpanded, comment.thread.size)
                    }
                }
                CommentLikes(
                    commentsSheetState = commentsSheetState,
                    commentId = comment.id,
                    isDeleted = comment.isDeleted,
                    onLikeClick = { onLikeClick(comment.copy(likes = it)) }
                )
            }
        }
    }
}

@Composable
private fun ExpandThreadButton(isCommentThreadExpanded: MutableState<Boolean>, threadSize: Int) {
    val textSwitch = rememberSwitch(R.string.show_thread, R.string.hide)
    val text = remember { mutableIntStateOf(textSwitch.last()) }
    Row(
        modifier = Modifier.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
            isCommentThreadExpanded.value = !isCommentThreadExpanded.value
            text.intValue = textSwitch.toggle()
        },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val iconRotation = animateFloatAsState(
            targetValue = if (isCommentThreadExpanded.value) -180f else 0f,
            label = "expandIconRotation"
        )
        Icon(
            modifier = Modifier.rotate(iconRotation.value),
            imageVector = Icons.Default.KeyboardArrowUp,
            contentDescription = "showThread",
            tint = LocalColorScheme.current.secondaryTextColor
        )
        AnimatedContent(
            targetState = text.intValue,
            label = "expandButtonText",
            transitionSpec = {
                fadeIn(tween(100)) togetherWith fadeOut(tween(100))
            }
        ) { targetValue ->
            Text(
                text = stringResource(targetValue, threadSize),
                fontSize = LocalTypography.current.tiny,
                color = LocalColorScheme.current.secondaryTextColor
            )
        }
    }
}

@Composable
private fun ThreadComments(
    thread: List<ThreadComment>,
    isExpanded: State<Boolean>,
    commentsSheetState: CommentsBottomSheetState,
    onLikeClick: (ThreadComment) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(start = 40.dp)
            .fillMaxWidth()
            .animateContentSize()
    ) {
        if (isExpanded.value) {
            Column(
                modifier = Modifier.padding(top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                for (comment in thread) {
                    ThreadCommentBody(comment, commentsSheetState, onLikeClick)
                }
            }
        }
    }
}

@Composable
private fun ThreadCommentBody(
    comment: ThreadComment,
    commentsSheetState: CommentsBottomSheetState,
    onLikeClick: (ThreadComment) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AsyncImage(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            model = ImageRequest.Builder(LocalContext.current)
                .fallback(R.drawable.avatar)
                .data(comment.owner?.photoUrl)
                .build(),
            contentScale = ContentScale.Crop,
            contentDescription = "commentOwnerAvatar",
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Column {
                if (comment.isDeleted) {
                    Text(
                        text = stringResource(R.string.comment_deleted),
                        color = LocalColorScheme.current.secondaryTextColor,
                        fontSize = LocalTypography.current.small
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = comment.owner?.let { "${it.firstName} ${it.lastName}" }
                                ?: stringResource(R.string.user_not_supports),
                            color = LocalColorScheme.current.secondaryTextColor,
                            fontSize = LocalTypography.current.tiny
                        )
                        Icon(
                            modifier = Modifier.size(12.dp),
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            tint = LocalColorScheme.current.secondaryTextColor,
                            contentDescription = "replyTo"
                        )
                        Text(
                            text = comment.replyToUser?.firstName
                                ?: stringResource(R.string.user_not_supports),
                            color = LocalColorScheme.current.secondaryTextColor,
                            fontSize = LocalTypography.current.tiny
                        )
                    }
                    val commentText = comment.text.ifEmpty {
                        stringResource(R.string.comment_is_not_supports)
                    }
                    val commentTextColor = if (comment.text.isEmpty()) {
                        LocalColorScheme.current.secondaryTextColor
                    } else {
                        LocalColorScheme.current.primaryTextColor
                    }

                    Text(
                        text = commentText,
                        color = commentTextColor,
                        fontSize = LocalTypography.current.small
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = comment.date,
                    color = LocalColorScheme.current.secondaryTextColor,
                    fontSize = LocalTypography.current.tiny
                )
                CommentLikes(
                    commentsSheetState = commentsSheetState,
                    commentId = comment.id,
                    isDeleted = comment.isDeleted,
                    onLikeClick = { onLikeClick(comment.copy(likes = it)) }
                )
            }
        }
    }
}

@Composable
private fun CommentLikes(
    commentsSheetState: CommentsBottomSheetState,
    commentId: Long,
    isDeleted: Boolean,
    onLikeClick: (Likes) -> Unit
) {
    val likes = rememberUpdatedState(commentsSheetState.likes[commentId])

    if (!isDeleted) {
        Row(
            modifier = Modifier
                .animateContentSize()
                .clickable { likes.value?.let(onLikeClick) },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            likes.value?.let { likesValue ->
                AnimatedContent(
                    targetState = likesValue.count,
                    transitionSpec = {
                        slideInVertically {
                            if (likesValue.userLikes) -it else it
                        } togetherWith slideOutVertically {
                            if (likesValue.userLikes) it else -it
                        }
                    },
                    label = "commentLikes"
                ) { targetState ->
                    Text(
                        text = targetState.toString(),
                        fontSize = LocalTypography.current.tiny,
                        color = if (likes.value?.userLikes == true) {
                            Color.Red
                        } else {
                            LocalColorScheme.current.secondaryTextColor
                        }
                    )
                }
            }
            Icon(
                modifier = Modifier.size(16.dp),
                imageVector = if (likes.value?.userLikes == true) {
                    Icons.Default.Favorite
                } else {
                    Icons.Default.FavoriteBorder
                },
                contentDescription = "commentLikesIcon",
                tint = if (likes.value?.userLikes == true) {
                    Color.Red
                } else {
                    LocalColorScheme.current.secondaryTextColor
                }
            )
        }
    }
}

@Composable
private fun OnEmptyCommentsMessage(comments: LazyPagingItems<Comment>) {
    if (comments.itemCount == 0 && comments.loadState.refresh is LoadState.NotLoading) {
        Box(modifier = Modifier.height(300.dp), contentAlignment = Alignment.Center) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    modifier = Modifier.fillMaxWidth(0.28f),
                    painter = painterResource(R.drawable.comment),
                    contentDescription = "comment",
                    tint = LocalColorScheme.current.secondaryTextColor
                )
                Text(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    text = stringResource(R.string.no_comments),
                    color = LocalColorScheme.current.secondaryTextColor,
                    fontSize = LocalTypography.current.average,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}



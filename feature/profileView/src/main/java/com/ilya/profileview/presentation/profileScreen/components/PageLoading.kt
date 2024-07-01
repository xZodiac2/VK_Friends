package com.ilya.profileview.presentation.profileScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.ilya.core.appCommon.StringResource
import com.ilya.core.basicComposables.OnError
import com.ilya.profileViewDomain.models.Post
import com.ilya.profileview.R
import com.ilya.theme.LocalColorScheme
import com.ilya.theme.LocalTypography

@Composable
internal fun ResolveRefresh(posts: LazyPagingItems<Post>) {
    when (posts.loadState.refresh) {
        LoadState.Loading -> OnLoading(modifier = Modifier.height(120.dp))

        is LoadState.Error -> {
            val state = posts.loadState.refresh as? LoadState.Error
            OnError(
                modifier = Modifier.height(120.dp),
                message = StringResource.FromId(
                    R.string.error_unknown,
                    listOf(state?.error?.message.toString())
                ),
                buttonText = StringResource.FromId(R.string.try_again),
                onButtonClick = { posts.refresh() }
            )
        }

        else -> Unit
    }
}

@Composable
internal fun ResolveAppend(posts: LazyPagingItems<Post>) {
    when (posts.loadState.append) {
        LoadState.Loading -> OnLoading(modifier = Modifier.height(120.dp))

        is LoadState.Error -> {
            val state = posts.loadState.append as? LoadState.Error
            OnError(
                modifier = Modifier.height(120.dp),
                message = StringResource.FromId(
                    R.string.error_unknown,
                    listOf(state?.error?.message.toString())
                ),
                buttonText = StringResource.FromId(R.string.try_again),
                onButtonClick = { posts.retry() }
            )
        }

        else -> Unit
    }
}

@Composable
private fun OnLoading(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = LocalColorScheme.current.primaryIconTintColor)
    }
}

@Composable
internal fun OnEmptyPostsMessage(posts: LazyPagingItems<Post>) {
    if (posts.itemCount == 0 && posts.loadState.refresh is LoadState.NotLoading) {
        Card(
            modifier = Modifier.aspectRatio(2f / 1),
            colors = CardDefaults.cardColors(
                containerColor = LocalColorScheme.current.cardContainerColor,
                contentColor = LocalColorScheme.current.secondaryTextColor
            )
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.8f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Icon(
                        painter = painterResource(R.drawable.empty),
                        modifier = Modifier.fillMaxSize(0.5f),
                        contentDescription = "noPosts",
                    )
                    Text(
                        text = stringResource(R.string.no_posts),
                        fontSize = LocalTypography.current.big
                    )
                }
            }
        }
    }
}


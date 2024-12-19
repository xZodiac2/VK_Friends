package com.ilya.friendsview.screen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
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
import com.ilya.core.appCommon.compose.basicComposables.OnError
import com.ilya.core.appCommon.compose.isEmpty
import com.ilya.friendsview.R
import com.ilya.friendsview.screen.ErrorType
import com.ilya.paging.PaginationError
import com.ilya.paging.models.User
import com.ilya.theme.LocalColorScheme
import com.ilya.theme.LocalTypography

@Composable
fun ResolveRefresh(friends: LazyPagingItems<User>, onEmptyAccessToken: () -> Unit) {
    when (val state = friends.loadState.refresh) {
        LoadState.Loading -> OnLoading(modifier = Modifier.height(500.dp))
        is LoadState.Error -> {
            OnPagingError(
                modifier = Modifier.height(500.dp),
                errorType = state.error.correspondingErrorType(),
                onTryAgainClick = { friends.refresh() },
                onEmptyAccessToken = onEmptyAccessToken
            )
        }

        is LoadState.NotLoading -> Unit
    }
}

@Composable
private fun OnPagingError(
    modifier: Modifier = Modifier,
    errorType: ErrorType,
    onTryAgainClick: () -> Unit,
    onEmptyAccessToken: () -> Unit,
) {
    when (errorType) {
        ErrorType.NoInternet -> OnError(
            modifier = modifier,
            message = StringResource.FromId(R.string.error_no_able_to_get_data),
            buttonText = StringResource.FromId(R.string.retry),
            onButtonClick = onTryAgainClick
        )

        ErrorType.NoAccessToken -> onEmptyAccessToken()
        is ErrorType.Unknown -> OnError(
            modifier = modifier,
            message = StringResource.FromId(
                id = R.string.error_unknown,
                formatArgs = listOf(errorType.error.message.toString())
            ),
            buttonText = StringResource.FromId(id = R.string.retry),
            onButtonClick = onTryAgainClick
        )
    }
}

@Composable
private fun OnLoading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) { CircularProgressIndicator(color = LocalColorScheme.current.primaryIconTintColor) }
}

@Composable
fun ResolveAppend(friends: LazyPagingItems<User>, onEmptyAccessToken: () -> Unit) {
    when (val state = friends.loadState.append) {
        LoadState.Loading -> OnLoading(modifier = Modifier.height(120.dp))
        is LoadState.Error -> {
            OnPagingError(
                modifier = Modifier.height(120.dp),
                errorType = state.error.correspondingErrorType(),
                onTryAgainClick = { friends.retry() },
                onEmptyAccessToken = onEmptyAccessToken
            )
        }

        is LoadState.NotLoading -> Unit
    }
}

@Composable
fun OnEmptyFriends(friends: LazyPagingItems<User>) {
    if (friends.isEmpty() && friends.loadState.refresh is LoadState.NotLoading) {
        Box(
            modifier = Modifier
                .height(350.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    modifier = Modifier.size(100.dp),
                    painter = painterResource(R.drawable.empty),
                    contentDescription = "emptiness",
                    tint = LocalColorScheme.current.secondaryTextColor
                )
                Text(
                    text = stringResource(R.string.no_friends_found),
                    fontSize = LocalTypography.current.big,
                    color = LocalColorScheme.current.secondaryTextColor
                )
            }
        }
    }
}

private fun Throwable.correspondingErrorType(): ErrorType {
    return when (this) {
        is PaginationError.NoInternet -> ErrorType.NoInternet
        is PaginationError.NoAccessToken -> ErrorType.NoAccessToken
        else -> ErrorType.Unknown(this)
    }
}

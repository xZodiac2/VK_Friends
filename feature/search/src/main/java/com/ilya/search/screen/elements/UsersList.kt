package com.ilya.search.screen.elements

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.ilya.core.appCommon.StringResource
import com.ilya.core.basicComposables.OnError
import com.ilya.data.paging.PaginationError
import com.ilya.data.paging.User
import com.ilya.search.R
import com.ilya.search.screen.ErrorType
import com.ilya.theme.LocalColorScheme

fun LazyGridScope.usersList(
    pagingItems: LazyPagingItems<User>,
    onCardClick: (Long) -> Unit,
    onEmptyAccessToken: () -> Unit,
    onTryAgainClick: () -> Unit,
    onDataLoaded: () -> Unit
) {
    items(count = pagingItems.itemCount) { index ->
        val user = pagingItems[index]
        if (user != null) {
            UserCard(
                onCardClick = onCardClick,
                user = user
            )
            LaunchedEffect(key1 = Unit) {
                onDataLoaded()
            }
        }
    }
    item(span = { GridItemSpan(2) }) {
        when (val loadState = pagingItems.loadState.append) {
            LoadState.Loading -> OnAppendLoading()
            is LoadState.Error -> OnAppendError(
                error = when (loadState.error) {
                    PaginationError.NoInternet -> ErrorType.NoInternet
                    PaginationError.NoAccessToken -> ErrorType.NoAccessToken
                    else -> ErrorType.Unknown(loadState.error)
                },
                onTryAgainClick = onTryAgainClick,
                onEmptyAccessToken = onEmptyAccessToken
            )

            is LoadState.NotLoading -> Unit
        }
    }
}

@Composable
private fun OnAppendLoading() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        contentAlignment = Alignment.Center
    ) { CircularProgressIndicator(color = LocalColorScheme.current.primaryIconTintColor) }
}

@Composable
private fun OnAppendError(
    error: ErrorType,
    onTryAgainClick: () -> Unit,
    onEmptyAccessToken: () -> Unit
) {
    when (error) {
        ErrorType.NoInternet -> OnError(
            modifier = Modifier.height(140.dp),
            onButtonClick = onTryAgainClick,
            message = StringResource.Resource(R.string.error_no_able_to_load_data),
            buttonText = StringResource.Resource(R.string.retry)
        )

        ErrorType.NoAccessToken -> onEmptyAccessToken()
        is ErrorType.Unknown -> OnError(
            modifier = Modifier.height(140.dp),
            message = StringResource.Resource(
                id = R.string.error_unknown,
                formatArgs = listOf(error.error.message ?: "")
            ),
            buttonText = StringResource.Resource(id = R.string.retry),
            onButtonClick = onTryAgainClick
        )
    }
}

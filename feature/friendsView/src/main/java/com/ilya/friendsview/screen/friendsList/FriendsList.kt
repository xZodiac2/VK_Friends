package com.ilya.friendsview.screen.friendsList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.ilya.core.appCommon.StringResource
import com.ilya.core.basicComposables.OnError
import com.ilya.data.paging.PaginationError
import com.ilya.data.paging.User
import com.ilya.friendsview.R
import com.ilya.friendsview.screen.ErrorType

@Composable
fun FriendsList(
    pagingState: LazyPagingItems<User>,
    onProfileViewButtonClick: (Long) -> Unit,
    onEmptyAccessToken: () -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(count = pagingState.itemCount) { index ->
            val user = pagingState[index]
            if (user != null) {
                UserCard(
                    user = user,
                    onCardClick = onProfileViewButtonClick
                )
            }
        }
        item(span = { GridItemSpan(2) }) {
            when (val appendLoadState = pagingState.loadState.append) {
                LoadState.Loading -> OnLoadingAppend()
                is LoadState.Error -> OnAppendError(
                    error = when (appendLoadState.error) {
                        PaginationError.NoAccessToken -> ErrorType.NoAccessToken
                        PaginationError.NoInternet -> ErrorType.NoInternet
                        else -> ErrorType.Unknown(appendLoadState.error)
                    },
                    onEmptyAccessToken = onEmptyAccessToken,
                    onAppendRetry = { pagingState.retry() }
                )

                is LoadState.NotLoading -> Unit
            }
        }
    }
}

@Composable
private fun OnAppendError(
    error: ErrorType,
    onEmptyAccessToken: () -> Unit,
    onAppendRetry: () -> Unit
) {
    when (error) {
        is ErrorType.NoInternet -> OnError(
            modifier = Modifier.height(140.dp),
            message = StringResource.Resource(R.string.error_no_able_to_get_data),
            buttonText = StringResource.Resource(R.string.retry),
            onTryAgainClick = onAppendRetry
        )

        is ErrorType.NoAccessToken -> onEmptyAccessToken()
        is ErrorType.Unknown -> OnError(
            modifier = Modifier.height(140.dp),
            message = StringResource.Resource(
                id = R.string.error_unknown,
                arguments = listOf(error.error.message ?: "")
            ),
            buttonText = StringResource.Resource(id = R.string.retry),
            onTryAgainClick = onAppendRetry
        )
    }
}


@Composable
private fun OnLoadingAppend() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

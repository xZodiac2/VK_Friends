package com.ilya.friendsview.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ilya.core.appCommon.compose.basicComposables.alertDialog.AlertDialogStateHandler
import com.ilya.core.appCommon.compose.basicComposables.snackbar.SnackbarEventEffect
import com.ilya.friendsview.FriendsScreenViewModel
import com.ilya.friendsview.R
import com.ilya.friendsview.screen.components.FriendCard
import com.ilya.friendsview.screen.components.OnEmptyFriends
import com.ilya.friendsview.screen.components.ResolveAppend
import com.ilya.friendsview.screen.components.ResolveRefresh
import com.ilya.friendsview.screen.event.FriendsScreenEvent
import com.ilya.friendsview.screen.event.FriendsScreenNavEvent
import com.ilya.paging.models.User
import com.ilya.theme.LocalColorScheme
import com.ilya.theme.LocalTypography
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreen(handleNavEvent: (FriendsScreenNavEvent) -> Unit, onExitConfirm: () -> Unit) {
  val viewModel: FriendsScreenViewModel = hiltViewModel()

  val friends = remember { viewModel.friendsFlow }
  val alertDialogState = viewModel.alertDialogState.collectAsState()
  val snackbarState = viewModel.snackbarState.collectAsState()

  val snackbarHostState = remember { SnackbarHostState() }
  val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

  BackHandler { viewModel.handleEvent(FriendsScreenEvent.BackPress(onExitConfirm)) }
  AlertDialogStateHandler(state = alertDialogState.value)

  Scaffold(
    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    topBar = {
      TopBar(
        accountOwnerState = viewModel.accountOwnerState,
        onAvatarClick = { handleNavEvent(FriendsScreenNavEvent.OpenProfile(it)) },
        onPlaceholderClick = { viewModel.handleEvent(FriendsScreenEvent.PlaceholderAvatarClick) },
        scrollBehavior = scrollBehavior
      )
    },
    containerColor = LocalColorScheme.current.primary
  ) { padding ->
    Content(
      friendsFlow = friends,
      padding = padding,
      onEmptyAccessToken = { handleNavEvent(FriendsScreenNavEvent.EmptyAccessToken) },
      onFriendClick = { handleNavEvent(FriendsScreenNavEvent.OpenProfile(it)) },
    )
  }

  SnackbarEventEffect(
    state = snackbarState.value,
    onConsumed = { viewModel.handleEvent(FriendsScreenEvent.SnackbarConsumed) },
    action = { snackbarHostState.showSnackbar(it) }
  )

  LaunchedEffect(Unit) {
    viewModel.handleEvent(FriendsScreenEvent.Start)
  }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
  accountOwnerState: StateFlow<User?>,
  onAvatarClick: (Long) -> Unit,
  onPlaceholderClick: () -> Unit,
  scrollBehavior: TopAppBarScrollBehavior
) {
  val accountOwner = accountOwnerState.collectAsState()

  TopAppBar(
    title = {
      Text(
        modifier = Modifier.padding(start = 12.dp),
        text = stringResource(id = R.string.friends_screen_name),
        color = LocalColorScheme.current.primaryTextColor,
        fontSize = LocalTypography.current.large,
        fontWeight = FontWeight.W500
      )
    },
    navigationIcon = {
      AsyncImage(
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .clickable { accountOwner.value?.id?.let(onAvatarClick) ?: onPlaceholderClick() },
        model = ImageRequest.Builder(LocalContext.current)
          .placeholder(R.drawable.avatar)
          .fallback(R.drawable.avatar)
          .data(accountOwner.value?.photoUrl)
          .build(),
        contentDescription = "ownerPhoto200",
        contentScale = ContentScale.Crop
      )
    },
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = LocalColorScheme.current.secondary,
      scrolledContainerColor = LocalColorScheme.current.secondary
    ),
    windowInsets = WindowInsets(left = 16.dp),
    scrollBehavior = scrollBehavior
  )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun Content(
  friendsFlow: Flow<PagingData<User>>,
  padding: PaddingValues,
  onFriendClick: (Long) -> Unit,
  onEmptyAccessToken: () -> Unit,
) {
  val friends = friendsFlow.collectAsLazyPagingItems()
  val initialDataLoaded = remember { derivedStateOf { friends.itemCount > 0 } }
  val isRefreshing = friends.loadState.refresh == LoadState.Loading && initialDataLoaded.value

  val pullRefreshState = rememberPullRefreshState(
    refreshing = isRefreshing,
    onRefresh = { friends.refresh() }
  )

  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(padding)
      .pullRefresh(pullRefreshState)
  ) {
    LazyVerticalGrid(
      columns = GridCells.Fixed(2),
      contentPadding = PaddingValues(16.dp),
      horizontalArrangement = Arrangement.spacedBy(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      items(count = friends.itemCount, key = friends.itemKey { it.id }) { Friend(friends, it, onFriendClick) }
      item(span = { GridItemSpan(2) }) { ResolveRefresh(friends, onEmptyAccessToken) }
      item(span = { GridItemSpan(2) }) { ResolveAppend(friends, onEmptyAccessToken) }
      item(span = { GridItemSpan(2) }) { OnEmptyFriends(friends) }
    }

    PullRefreshIndicator(
      refreshing = isRefreshing,
      state = pullRefreshState,
      modifier = Modifier.align(Alignment.TopCenter),
      contentColor = LocalColorScheme.current.primaryIconTintColor,
      backgroundColor = LocalColorScheme.current.cardContainerColor
    )
  }
}

@Composable
private fun Friend(
  friends: LazyPagingItems<User>,
  index: Int,
  onFriendClick: (Long) -> Unit
) {
  val friend = friends[index]
  if (friend != null) {
    FriendCard(
      user = friend,
      onCardClick = { onFriendClick(friend.id) }
    )
  }
}




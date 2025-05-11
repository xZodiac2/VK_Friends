package com.ilya.profileview.profileScreen.components.profileCommon.profileHeader

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ilya.core.appCommon.compose.basicComposables.BaseButton
import com.ilya.core.appCommon.compose.basicComposables.BaseButtonStyles
import com.ilya.core.appCommon.enums.FriendStatus
import com.ilya.profileViewDomain.User
import com.ilya.profileview.R
import com.ilya.profileview.profileScreen.screens.event.receiver.ProfileScreenEventReceiver
import com.ilya.theme.LocalColorScheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProfileHeader(user: User, eventReceiver: ProfileScreenEventReceiver) {
  var showSheet by remember { mutableStateOf(false) }

  Card(
    shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
    colors = CardDefaults.cardColors(containerColor = LocalColorScheme.current.cardContainerColor)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 20.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      AvatarWithName(user.photoUrl, user.firstName, user.lastName)
      Status(user.status)
      ShowDetailsButton { showSheet = true }
      FriendRequestButton(user.isAccountOwner, user.friendStatus) { eventReceiver.onFriendRequest(user) }
    }
  }

  if (showSheet) {
    ModalBottomSheet(
      onDismissRequest = { showSheet = false },
      containerColor = LocalColorScheme.current.cardContainerColor,
      dragHandle = { BottomSheetDefaults.DragHandle(color = LocalColorScheme.current.iconTintColor) }
    ) { SheetContent(user) }
  }
}

@Composable
private fun AvatarWithName(photoUrl: String, firstName: String, lastName: String) {
  AsyncImage(
    modifier = Modifier
      .padding(top = 20.dp)
      .size(140.dp)
      .clip(CircleShape),
    model = photoUrl,
    contentDescription = "avatar",
    contentScale = ContentScale.Crop
  )
  Text(
    text = "$firstName $lastName",
    fontSize = 28.sp,
    modifier = Modifier.padding(top = 8.dp),
    color = LocalColorScheme.current.primaryTextColor
  )
}

@Composable
private fun Status(status: String) {
  if (status.isNotBlank()) {
    Text(
      modifier = Modifier
        .padding(top = 8.dp)
        .widthIn(max = 350.dp),
      text = status,
      textAlign = TextAlign.Center,
      overflow = TextOverflow.Ellipsis,
      maxLines = 2,
      color = LocalColorScheme.current.primaryTextColor
    )
  }
}

@Composable
private fun ShowDetailsButton(onClick: () -> Unit) {
  TextButton(onClick) {
    Row {
      Icon(
        modifier = Modifier
          .padding(top = 2.dp, end = 2.dp)
          .size(16.dp),
        imageVector = Icons.Outlined.Info,
        contentDescription = "more",
        tint = LocalColorScheme.current.iconTintColor
      )
      Text(
        text = stringResource(R.string.details),
        color = LocalColorScheme.current.primaryTextColor,
        fontWeight = FontWeight.W400,
      )
    }
  }
}

@Composable
private fun FriendRequestButton(isAccountOwner: Boolean, friendStatus: FriendStatus, onClick: () -> Unit) {
  if (!isAccountOwner) {
    BaseButton(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 40.dp),
      onClick = onClick,
      style = BaseButtonStyles.Attractive
    ) {
      Icon(
        modifier = Modifier
          .padding(end = 8.dp)
          .size(20.dp),
        painter = when (friendStatus) {
          FriendStatus.NOT_FRIENDS -> painterResource(id = R.drawable.plus)
          FriendStatus.WAITING -> painterResource(id = R.drawable.clock)
          FriendStatus.FRIENDS -> painterResource(id = R.drawable.crossed_out_human)
          FriendStatus.SUBSCRIBED -> painterResource(id = R.drawable.plus)
        },
        contentDescription = "add",
      )
      Text(
        text = stringResource(
          id = when (friendStatus) {
            FriendStatus.NOT_FRIENDS -> R.string.status_not_friend
            FriendStatus.FRIENDS -> R.string.status_friends
            FriendStatus.WAITING -> R.string.status_waiting_for_response
            FriendStatus.SUBSCRIBED -> R.string.status_friend_request
          }
        ),
        fontSize = 18.sp
      )
    }
  }
}


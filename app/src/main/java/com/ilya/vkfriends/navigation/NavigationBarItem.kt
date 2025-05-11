package com.ilya.vkfriends.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable

sealed class NavigationBarItem(
  val destination: Destination,
  val icon: @Composable () -> Unit,
) {

  data object FriendsView : NavigationBarItem(
    destination = Destination.FriendsScreen,
    icon = {
      Icon(
        imageVector = Icons.Outlined.Person,
        contentDescription = "FriendsScreenIcon"
      )
    }
  )

  data object Search : NavigationBarItem(
    destination = Destination.SearchScreen,
    icon = {
      Icon(
        imageVector = Icons.Outlined.Search,
        contentDescription = "SearchFriendsScreenIcon"
      )
    }
  )

}

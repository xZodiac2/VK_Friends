package com.ilya.vkfriends.navigation.eventHandlers

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.ilya.core.appCommon.base.EventHandler
import com.ilya.friendsview.screen.event.FriendsScreenNavEvent
import com.ilya.vkfriends.navigation.Destination

class FriendsScreenNavEventHandler(private val navController: NavController) : EventHandler<FriendsScreenNavEvent> {

  override fun handleEvent(event: FriendsScreenNavEvent) {
    when (event) {
      FriendsScreenNavEvent.EmptyAccessToken -> onEmptyAccessToken()
      is FriendsScreenNavEvent.OpenProfile -> onOpenProfile(event.id)
    }
  }

  private fun onEmptyAccessToken() {
    navController.navigate(Destination.AuthScreen) {
      popUpTo(navController.graph.findStartDestination().id) {
        inclusive = true
      }
    }
  }

  private fun onOpenProfile(id: Long) {
    navController.navigate(Destination.ProfileScreen(id, false))
  }

}
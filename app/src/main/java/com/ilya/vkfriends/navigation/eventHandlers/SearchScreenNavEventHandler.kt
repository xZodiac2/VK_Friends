package com.ilya.vkfriends.navigation.eventHandlers

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.ilya.core.appCommon.base.EventHandler
import com.ilya.search.screen.event.SearchScreenNavEvent
import com.ilya.vkfriends.navigation.Destination

class SearchScreenNavEventHandler(private val navController: NavController) : EventHandler<SearchScreenNavEvent> {

    override fun handleEvent(event: SearchScreenNavEvent) {
        when (event) {
            SearchScreenNavEvent.EmptyAccessToken -> onEmptyAccessToken()
            is SearchScreenNavEvent.ProfileClick -> onProfileClick(event.id, event.isPrivate)
        }
    }

    private fun onProfileClick(id: Long, isPrivate: Boolean) {
        navController.navigate(Destination.ProfileScreen(id, isPrivate))
    }

    private fun onEmptyAccessToken() {
        navController.navigate(Destination.AuthScreen) {
            popUpTo(navController.graph.findStartDestination().id) {
                inclusive = true
            }
        }
    }


}
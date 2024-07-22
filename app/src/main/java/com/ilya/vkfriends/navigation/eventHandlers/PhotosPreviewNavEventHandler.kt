package com.ilya.vkfriends.navigation.eventHandlers

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.ilya.core.appCommon.EventHandler
import com.ilya.profileview.photosPreview.event.PhotosPreviewNavEvent
import com.ilya.vkfriends.navigation.Destination

class PhotosPreviewNavEventHandler(private val navController: NavController) : EventHandler<PhotosPreviewNavEvent> {
    override fun handleEvent(event: PhotosPreviewNavEvent) {
        when (event) {
            PhotosPreviewNavEvent.BackClick -> onBackClick()
            PhotosPreviewNavEvent.NavigateToAuth -> onNavigateToAuth()
        }
    }

    private fun onNavigateToAuth() {
        navController.navigate(Destination.AuthScreen) {
            popUpTo(navController.graph.findStartDestination().id) {
                inclusive = true
            }
        }
    }

    private fun onBackClick() = navController.popBackStack()

}
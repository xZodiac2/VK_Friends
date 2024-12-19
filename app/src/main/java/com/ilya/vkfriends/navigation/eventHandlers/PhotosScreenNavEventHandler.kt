package com.ilya.vkfriends.navigation.eventHandlers

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.ilya.core.appCommon.base.EventHandler
import com.ilya.profileview.photosScreen.event.PhotosScreenNavEvent
import com.ilya.vkfriends.navigation.Destination

class PhotosScreenNavEventHandler(private val navController: NavController) : EventHandler<PhotosScreenNavEvent> {

    override fun handleEvent(event: PhotosScreenNavEvent) {
        when (event) {
            PhotosScreenNavEvent.BackClick -> onBackClick()
            PhotosScreenNavEvent.EmptyAccessToken -> onEmptyAccessToken()
            is PhotosScreenNavEvent.OpenPhoto -> onOpenPhoto(event.userId, event.photoIndex)
        }
    }

    private fun onOpenPhoto(userId: Long, photoIndex: Int) {
        navController.navigate(Destination.PhotosPreview(userId, photoIndex))
    }

    private fun onEmptyAccessToken() {
        navController.navigate(Destination.AuthScreen) {
            popUpTo(navController.graph.findStartDestination().id) {
                inclusive = true
            }
        }
    }

    private fun onBackClick() = navController.popBackStack()
}
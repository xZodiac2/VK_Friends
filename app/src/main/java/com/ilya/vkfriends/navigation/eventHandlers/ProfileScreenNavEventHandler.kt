package com.ilya.vkfriends.navigation.eventHandlers

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.ilya.core.appCommon.base.EventHandler
import com.ilya.profileview.profileScreen.screens.event.ProfileScreenNavEvent
import com.ilya.vkfriends.navigation.Destination

class ProfileScreenNavEventHandler(private val navController: NavController) : EventHandler<ProfileScreenNavEvent> {

    override fun handleEvent(event: ProfileScreenNavEvent) {
        when (event) {
            ProfileScreenNavEvent.BackClick -> onBackClick()
            ProfileScreenNavEvent.EmptyAccessToken -> onEmptyAccessToken()
            is ProfileScreenNavEvent.OpenPhotosClick -> onOpenPhotosClick(event.userId)
            is ProfileScreenNavEvent.PhotoClick -> onPhotoClick(event.userId, event.targetPhotoIndex)
            is ProfileScreenNavEvent.PostPhotoClick -> onPostPhotoClick(event.userId, event.targetPhotoIndex, event.photoIds)
            is ProfileScreenNavEvent.VideoClick -> onVideoClick(event.userId, event.id, event.accessKey)
            is ProfileScreenNavEvent.PostAuthorClick -> onPostAuthorClick(event.id, event.isPrivate)
        }
    }

    private fun onPostAuthorClick(id: Long, isPrivate: Boolean) {
        navController.navigate(Destination.ProfileScreen(id, isPrivate))
    }

    private fun onVideoClick(userId: Long, id: Long, accessKey: String) {
        val key = accessKey.ifEmpty { BLANK_ACCESS_KEY }
        navController.navigate(Destination.VideoPreview(userId, id, key))
    }

    private fun onPostPhotoClick(userId: Long, targetPhotoIndex: Int, photoIds: Map<Long, String>) {
        navController.navigate(Destination.PhotosPreview(
            userId = userId,
            targetPhotoIndex = targetPhotoIndex,
            photoIds = photoIds.toIdsString()
        ))
    }

    private fun onPhotoClick(userId: Long, targetIndex: Int) {
        navController.navigate(Destination.PhotosPreview(userId, targetIndex))
    }

    private fun onOpenPhotosClick(userId: Long) = navController.navigate(Destination.PhotosScreen(userId))

    private fun onEmptyAccessToken() {
        navController.navigate(Destination.AuthScreen) {
            popUpTo(navController.graph.findStartDestination().id) {
                inclusive = true
            }
        }
    }

    private fun onBackClick() = navController.popBackStack()

    private fun Map<Long, String>.toIdsString(): String {
        val ids = keys.map {
            val accessKey = this[it] ?: ""
            if (accessKey.isNotBlank()) {
                "${it}_${accessKey}"
            } else {
                "${it}_$BLANK_ACCESS_KEY"
            }
        }
        return ids.joinToString(",")
    }

    companion object {
        const val BLANK_ACCESS_KEY = "blankAccessKey"
    }

}
package com.ilya.vkfriends.navigation

import androidx.navigation.NavBackStackEntry
import kotlinx.serialization.Serializable


@Serializable
sealed interface Destination {

    @Serializable
    data object AuthScreen : Destination

    @Serializable
    data class ProfileScreen(val userId: Long) : Destination

    @Serializable
    data object SearchScreen : Destination

    @Serializable
    data object FriendsScreen : Destination

    @Serializable
    data class PhotosPreview(val userId: Long, val targetPhotoIndex: Int) : Destination

    @Serializable
    data class PhotosScreen(val userId: Long) : Destination

}

val NavBackStackEntry.lastDestinationName: String
    get() {
        return destination.route
            ?.substringBefore("/")
            ?.substringBefore("?")
            ?.substringAfterLast(".") ?: ""
    }



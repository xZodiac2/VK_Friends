package com.ilya.vkfriends.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.toRoute
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

}

val NavBackStackEntry.lastDestinationName: String
    get() {
        return destination.route
            ?.substringBefore("/")
            ?.substringBefore("?")
            ?.substringAfterLast(".") ?: ""
    }

val NavBackStackEntry.lastDestination: Destination
    get() {
        return when (lastDestinationName) {
            Destination.AuthScreen::class.simpleName -> Destination.AuthScreen
            Destination.FriendsScreen::class.simpleName -> Destination.FriendsScreen
            Destination.ProfileScreen::class.simpleName -> Destination.ProfileScreen(
                toRoute<Destination.ProfileScreen>().userId
            )

            Destination.SearchScreen::class.simpleName -> Destination.SearchScreen
            else -> Destination.AuthScreen
        }

    }

package com.ilya.vkfriends.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut

sealed class Destination(
    val route: String,
    val transition: NavigationTransition? = null
) {
    object AuthScreen : Destination(route = "auth")

    object FriendsViewScreen : Destination(
        route = "friends",
        transition = NavigationTransition(
            enterTransition = { fadeIn(animationSpec = tween(0)) },
            exitTransition = { fadeOut(animationSpec = tween(0)) }
        )
    )

    object ProfileViewScreen : Destination(
        route = "profileView",
        transition = NavigationTransition(
            enterTransition = { fadeIn(animationSpec = tween(0)) },
            exitTransition = { fadeOut(animationSpec = tween(0)) }
        )
    )

    object SearchScreen : Destination(
        route = "search",
        transition = NavigationTransition(
            enterTransition = { fadeIn(animationSpec = tween(0)) },
            exitTransition = { fadeOut(animationSpec = tween(0)) },
        )
    )

    fun withArgumentNames(vararg names: String): String {
        return route + names.joinToString(prefix = "/{", separator = "}/{", postfix = "}")
    }

    fun withArguments(vararg args: String): String {
        return route + args.joinToString(prefix = "/", separator = "/")
    }

}


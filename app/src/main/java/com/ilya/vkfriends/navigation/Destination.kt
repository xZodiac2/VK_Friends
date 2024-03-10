package com.ilya.vkfriends.navigation

sealed class Destination(open val route: String) {
    object AuthScreen : Destination("auth")
    object FriendsViewScreen : Destination("friends")
    object ProfileViewScreen : Destination("profileView")
    
    fun withArgumentNames(vararg names: String): String {
        return route + names.joinToString(prefix = "/{", separator = "}/{", postfix = "}")
    }
    
    fun withArguments(vararg args: String): String {
        return route + args.joinToString(prefix = "/", separator = "/")
    }
    
}
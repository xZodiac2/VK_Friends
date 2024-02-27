package com.ilya.vkfriends.navigation

sealed class Destination(val route: String) {
    object AuthScreen : Destination("auth")
    object FriendsViewScreen : Destination("friends")
    
    fun withArgumentNames(vararg names: String): String {
        return route + names.joinToString(prefix = "/{", separator = "}/{", postfix = "}")
    }
    
    fun withArguments(vararg args: String): String {
        return route + args.joinToString(prefix = "/", separator = "/")
    }
    
}
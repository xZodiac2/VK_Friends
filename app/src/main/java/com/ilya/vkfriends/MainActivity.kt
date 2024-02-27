package com.ilya.vkfriends

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ilya.auth.screen.AuthorizationScreen
import com.ilya.friendsview.screen.FriendsScreen
import com.ilya.vkfriends.navigation.Destination
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            
            NavHost(navController = navController, startDestination = Destination.AuthScreen.route) {
                composable(Destination.AuthScreen.route) {
                    AuthorizationScreen(onAuthorize = {
                        navController.navigate(Destination.FriendsViewScreen.route)
                    })
                }
                composable(Destination.FriendsViewScreen.route) {
                    FriendsScreen()
                }
            }
        }
    }
}

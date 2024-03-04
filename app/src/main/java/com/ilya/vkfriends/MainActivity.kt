package com.ilya.vkfriends

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ilya.auth.screen.AuthorizationScreen
import com.ilya.friendsview.screen.FriendsScreen
import com.ilya.theme.VkFriendsAppTheme
import com.ilya.vkfriends.app.VkFriendsApplication
import com.ilya.vkfriends.navigation.Destination
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var shPrefs: SharedPreferences
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VkFriendsAppTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Destination.AuthScreen.route
                ) {
                    composable(Destination.AuthScreen.route) {
                        Log.d("mytag", "composable 1")
                        AuthorizationScreen(onAuthorize = {
                            navController.navigate(Destination.FriendsViewScreen.route) {
                                popUpTo(Destination.AuthScreen.route) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        })
                    }
                    composable(Destination.FriendsViewScreen.route) {
                        Log.d("mytag", "composable 2")
                        FriendsScreen(onAgree = {
                            navController.navigate(Destination.AuthScreen.route)
                        })
                    }
                }
            }
        }
    }
    
    override fun onStop() {
        super.onStop()
        Log.d("msg", "Destroy")
        if (VkFriendsApplication.IS_DEVELOPING) {
            with(shPrefs.edit()) {
                clear()
                apply()
            }
        }
    }
    
}

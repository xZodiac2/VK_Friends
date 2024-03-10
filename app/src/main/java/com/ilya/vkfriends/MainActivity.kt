package com.ilya.vkfriends

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.ilya.auth.screen.AuthorizationScreen
import com.ilya.core.appCommon.AccessTokenManager
import com.ilya.friendsview.screen.FriendsScreen
import com.ilya.profileview.screen.ProfileViewScreen
import com.ilya.theme.LocalColorScheme
import com.ilya.theme.VkFriendsAppTheme
import com.ilya.vkfriends.app.VkFriendsApplication
import com.ilya.vkfriends.navigation.Destination
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var accessTokenManager: AccessTokenManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            
            VkFriendsAppTheme {
                Scaffold(
                    containerColor = LocalColorScheme.current.primary,
                    topBar = {
                       // TopBar(navController = navController)
                    }
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = Destination.FriendsViewScreen.route,
                        modifier = Modifier.padding(it)
                    ) {
                        composable(Destination.AuthScreen.route) {
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
                            FriendsScreen(
                                onEmptyAccessToken = {
                                    navController.navigate(Destination.AuthScreen.route) {
                                        popUpTo(Destination.FriendsViewScreen.route) {
                                            inclusive = true
                                        }
                                    }
                                },
                                onProfileViewButtonClick = { userId ->
                                    navController.navigate(
                                        Destination.ProfileViewScreen.withArguments(
                                            userId.toString()
                                        )
                                    )
                                }
                            )
                        }
                        composable(
                            Destination.ProfileViewScreen.withArgumentNames("userId"),
                            arguments = listOf(navArgument("userId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            ProfileViewScreen(
                                userId = backStackEntry.arguments?.getString("userId") ?: "",
                                onClick = {
                                    navController.navigate(Destination.FriendsViewScreen.route) {
                                        launchSingleTop = true
                                        popUpTo(Destination.FriendsViewScreen.route)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
    
    @Composable
    private fun TopBar(
        modifier: Modifier = Modifier,
        navController: NavController,
    ) {
        val currentBackStack by navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStack?.destination ?: return
        when (currentDestination.route) {
            Destination.FriendsViewScreen.route -> FriendsViewTopBar(modifier, navController)
            Destination.ProfileViewScreen.route -> {}
        }
    }
    
    @Composable
    private fun FriendsViewTopBar(modifier: Modifier, navController: NavController) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(LocalColorScheme.current.secondary)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = accessTokenManager.accessToken?.userData?.photo200,
                contentDescription = "currentUserPhoto",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable {
                        navController.navigate(
                            Destination.ProfileViewScreen.withArguments(
                                accessTokenManager.accessToken?.userID?.toString() ?: ""
                            )
                        ) { launchSingleTop = true }
                    }
            )
        }
    }
    
}

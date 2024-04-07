package com.ilya.vkfriends

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.search.screen.SearchScreen
import com.ilya.auth.screen.AuthorizationScreen
import com.ilya.core.appCommon.AccessTokenManager
import com.ilya.core.appCommon.AccessTokenOperationsListener
import com.ilya.friendsview.screen.FriendsScreen
import com.ilya.profileview.presentation.screen.ProfileViewScreen
import com.ilya.theme.LocalColorScheme
import com.ilya.theme.VkFriendsAppTheme
import com.ilya.vkfriends.navigation.Destination
import com.ilya.vkfriends.navigation.NavigationBarItem
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var accessTokenManager: AccessTokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("token", accessTokenManager.accessToken?.token ?: "no token")
        setContent {
            VkFriendsAppTheme {
                val navController = rememberNavController()

                val accessTokenListener = AccessTokenOperationsListener { token ->
                    token ?: navController.navigate(Destination.AuthScreen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                    }
                }

                Scaffold(
                    bottomBar = { BottomBar(navController = navController) },
                    containerColor = LocalColorScheme.current.primary
                ) { paddingValues ->
                    Navigation(navController = navController, paddingValues = paddingValues)
                }

                DisposableEffect(key1 = lifecycle) {
                    accessTokenManager.addAccessTokenListener(accessTokenListener)
                    onDispose {
                        accessTokenManager.removeAccessTokenListener(accessTokenListener)
                    }
                }
            }
        }
    }

    @Composable
    private fun BottomBar(navController: NavController) {
        val currentBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStackEntry?.destination
        if (currentDestination?.route == Destination.AuthScreen.route) return

        val navigationBarItems = listOf(NavigationBarItem.FriendsView, NavigationBarItem.Search)
        NavigationBar(
            containerColor = LocalColorScheme.current.primary,
            modifier = Modifier.border(1.dp, LocalColorScheme.current.secondary)
        ) {
            navigationBarItems.forEach { item ->
                NavigationBarItem(
                    selected = currentDestination?.hierarchy?.any {
                        it.route == item.destination.route
                    } == true,
                    onClick = {
                        navController.navigate(item.destination.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = item.icon,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = LocalColorScheme.current.selectedIconColor,
                        unselectedIconColor = LocalColorScheme.current.unselectedIconColor,
                        indicatorColor = LocalColorScheme.current.bottomNavSelectedIndicatorColor
                    )
                )
            }
        }
    }

    @Composable
    private fun Navigation(navController: NavHostController, paddingValues: PaddingValues) {
        NavHost(
            navController = navController,
            startDestination = Destination.FriendsViewScreen.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(
                Destination.AuthScreen.route,
                enterTransition = Destination.AuthScreen.transition?.enterTransition,
                exitTransition = Destination.AuthScreen.transition?.exitTransition
            ) {
                AuthorizationScreen(onAuthorize = {
                    navController.navigate(Destination.FriendsViewScreen.route) {
                        popUpTo(Destination.AuthScreen.route) {
                            inclusive = true
                        }
                    }
                })
            }
            composable(
                Destination.FriendsViewScreen.route,
                enterTransition = Destination.FriendsViewScreen.transition?.enterTransition,
                exitTransition = Destination.FriendsViewScreen.transition?.exitTransition
            ) {
                FriendsScreen(onEmptyAccessToken = {
                    navController.navigate(Destination.AuthScreen.route) {
                        popUpTo(Destination.FriendsViewScreen.route) {
                            inclusive = true
                        }
                    }
                }, onProfileViewButtonClick = { userId ->
                    navController.navigate(
                        Destination.ProfileViewScreen.withArguments(userId.toString())
                    )
                }, onExitConfirm = { finish() })
            }
            composable(
                Destination.ProfileViewScreen.withArgumentNames("userId"),
                arguments = listOf(navArgument("userId") { type = NavType.StringType }),
                enterTransition = Destination.ProfileViewScreen.transition?.enterTransition,
                exitTransition = Destination.ProfileViewScreen.transition?.exitTransition
            ) { backStackEntry ->
                ProfileViewScreen(
                    userId = backStackEntry.arguments?.getString("userId") ?: "",
                    onClick = { navController.popBackStack() }
                )
            }
            composable(
                Destination.SearchScreen.route,
                enterTransition = Destination.SearchScreen.transition?.enterTransition,
                exitTransition = Destination.SearchScreen.transition?.exitTransition
            ) {
                SearchScreen(openProfileRequest = {
                    navController.navigate(
                        Destination.ProfileViewScreen.withArguments(it.toString())
                    )
                }, onEmptyAccessToken = {
                    navController.navigate(Destination.AuthScreen.route) {
                        popUpTo(Destination.FriendsViewScreen.route) {
                            inclusive = true
                        }
                    }
                })
            }
        }
    }

}

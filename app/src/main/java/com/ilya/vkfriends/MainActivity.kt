package com.ilya.vkfriends

import android.media.MediaPlayer
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.ilya.auth.screen.AuthorizationScreen
import com.ilya.core.appCommon.AccessTokenManager
import com.ilya.core.util.logThrowable
import com.ilya.friendsview.screen.FriendsScreen
import com.ilya.profileview.photosPreview.PhotosPreview
import com.ilya.profileview.photosScreen.PhotosScreen
import com.ilya.profileview.profileScreen.screens.ProfileScreen
import com.ilya.profileview.videoPreview.VideoPreview
import com.ilya.search.screen.SearchScreen
import com.ilya.theme.LocalColorScheme
import com.ilya.theme.VkFriendsAppTheme
import com.ilya.vkfriends.navigation.Destination
import com.ilya.vkfriends.navigation.NavigationBarItem
import com.ilya.vkfriends.navigation.ScreenTransition
import com.ilya.vkfriends.navigation.lastDestinationName
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var accessTokenManager: AccessTokenManager

    @Inject
    lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("token", accessTokenManager.accessToken?.token ?: "no token")
        setContent {
            VkFriendsAppTheme {
                val navController = rememberNavController()

                var bottomBarVisible by remember { mutableStateOf(false) }

                Scaffold(
                    bottomBar = {
                        if (bottomBarVisible) {
                            BottomBar(navController = navController)
                        }
                    },
                    containerColor = LocalColorScheme.current.primary
                ) { paddingValues ->
                    Navigation(
                        navController = navController,
                        paddingValues = paddingValues,
                        hideBottomBar = { bottomBarVisible = false },
                        showBottomBar = { bottomBarVisible = true }
                    )
                }

            }
        }
    }

    @Composable
    private fun BottomBar(navController: NavController) {
        val currentBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStackEntry?.destination
        val navigationBarItems = listOf(NavigationBarItem.FriendsView, NavigationBarItem.Search)

        NavigationBar(
            containerColor = LocalColorScheme.current.primary,
            modifier = Modifier.border(1.dp, LocalColorScheme.current.secondary)
        ) {
            navigationBarItems.forEach { item ->
                NavigationBarItem(
                    selected = currentDestination?.hierarchy?.any {
                        currentBackStackEntry?.lastDestinationName == item.destination::class.simpleName
                    } == true,
                    onClick = {
                        navController.navigate(item.destination) {
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
    private fun Navigation(
        navController: NavHostController,
        paddingValues: PaddingValues,
        hideBottomBar: () -> Unit,
        showBottomBar: () -> Unit,
    ) {
        NavHost(
            navController = navController,
            startDestination = if (accessTokenManager.accessToken == null) {
                Destination.AuthScreen
            } else {
                Destination.FriendsScreen
            },
            modifier = Modifier.padding(paddingValues)
        ) {
            composable<Destination.AuthScreen>(
                enterTransition = ScreenTransition.AuthScreen.transition.enterTransition,
                exitTransition = ScreenTransition.AuthScreen.transition.exitTransition
            ) {
                AuthorizationScreen(onAuthorized = {
                    navController.navigate(Destination.FriendsScreen) {
                        popUpTo(Destination.AuthScreen) {
                            inclusive = true
                        }
                    }
                })
            }
            composable<Destination.FriendsScreen>(
                enterTransition = ScreenTransition.FriendsScreen.transition.enterTransition,
                exitTransition = ScreenTransition.FriendsScreen.transition.exitTransition
            ) {
                FriendsScreen(
                    onEmptyAccessToken = {
                        navController.navigate(Destination.AuthScreen) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = true
                            }
                        }
                    },
                    openProfileRequest = { userId ->
                        navController.navigate(Destination.ProfileScreen(userId, false))
                    },
                    onExitConfirm = ::finish
                )
                LaunchedEffect(Unit) {
                    showBottomBar()
                }
            }
            composable<Destination.ProfileScreen>(
                enterTransition = ScreenTransition.ProfileScreen.transition.enterTransition,
                exitTransition = ScreenTransition.ProfileScreen.transition.exitTransition
            ) { backStackEntry ->
                val route = backStackEntry.toRoute<Destination.ProfileScreen>()
                ProfileScreen(
                    userId = route.userId,
                    isPrivate = route.isPrivate,
                    onBackClick = navController::popBackStack,
                    onEmptyAccessToken = {
                        navController.navigate(Destination.AuthScreen) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = true
                            }
                        }
                    },
                    onPhotoClick = { userId, targetIndex ->
                        navController.navigate(Destination.PhotosPreview(userId, targetIndex))
                    },
                    onOpenPhotosClick = {
                        navController.navigate(Destination.PhotosScreen(it))
                    },
                    onPostPhotoClick = { userId, targetIndex, ids ->
                        navController.navigate(Destination.PhotosPreview(
                            userId = userId,
                            targetPhotoIndex = targetIndex,
                            photoIds = ids.toIdsString()
                        ))
                    },
                    onVideoClick = { ownerId, id, accessKey ->
                        val key = accessKey.ifEmpty { BLANK_ACCESS_KEY }
                        navController.navigate(Destination.VideoPreview(ownerId, id, key))
                    }
                )
                LaunchedEffect(Unit) {
                    showBottomBar()
                }
            }
            composable<Destination.SearchScreen>(
                enterTransition = ScreenTransition.SearchScreen.transition.enterTransition,
                exitTransition = ScreenTransition.SearchScreen.transition.exitTransition
            ) {
                SearchScreen(
                    openProfileRequest = { id, isPrivate ->
                        navController.navigate(Destination.ProfileScreen(id, isPrivate))
                    },
                    onEmptyAccessToken = {
                        navController.navigate(Destination.AuthScreen) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = true
                            }
                        }
                    }
                )
                LaunchedEffect(Unit) {
                    showBottomBar()
                }
            }
            composable<Destination.PhotosPreview>(
                enterTransition = ScreenTransition.PhotosPreview.transition.enterTransition,
                exitTransition = ScreenTransition.PhotosPreview.transition.exitTransition
            ) {
                val route = it.toRoute<Destination.PhotosPreview>()

                PhotosPreview(
                    userId = route.userId,
                    targetPhotoIndex = route.targetPhotoIndex,
                    photoIds = route.photoIds.fromIdsString(),
                    onBackClick = navController::popBackStack
                )

                LaunchedEffect(Unit) {
                    hideBottomBar()
                }
            }
            composable<Destination.PhotosScreen>(
                enterTransition = ScreenTransition.PhotosScreen.transition.enterTransition,
                exitTransition = ScreenTransition.PhotosScreen.transition.exitTransition
            ) {
                val route = it.toRoute<Destination.PhotosScreen>()

                PhotosScreen(
                    userId = route.userId,
                    onBackClick = navController::popBackStack,
                    onPhotoClick = { userId, photoIndex ->
                        navController.navigate(Destination.PhotosPreview(userId, photoIndex))
                    }
                )
            }
            composable<Destination.VideoPreview> {
                val route = it.toRoute<Destination.VideoPreview>()

                VideoPreview(
                    ownerId = route.ownerId,
                    videoId = route.id,
                    accessKey = route.accessKey.takeIf { it != BLANK_ACCESS_KEY } ?: "",
                    onBackClick = navController::popBackStack
                )

                LaunchedEffect(Unit) {
                    hideBottomBar()
                }
            }
        }
    }

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

    private fun String.fromIdsString(): Map<Long, String> {
        return try {
            val ids = this.split(",")
            ids.associate {
                val idWithAccessKey = it.split("_")
                if (idWithAccessKey[1] == BLANK_ACCESS_KEY) {
                    idWithAccessKey[0].toLong() to ""
                } else {
                    idWithAccessKey[0].toLong() to idWithAccessKey[1]
                }
            }
        } catch (e: IndexOutOfBoundsException) {
            logThrowable(e)
            emptyMap()
        } catch (e: NumberFormatException) {
            logThrowable(e)
            emptyMap()
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            } else {
                mediaPlayer.reset()
            }
        } catch (e: IllegalStateException) {
            logThrowable(e)
        }
    }

    override fun onRestart() {
        super.onRestart()
        try {
            mediaPlayer.start()
        } catch (e: IllegalStateException) {
            logThrowable(e)
        }
    }

    companion object {
        private const val BLANK_ACCESS_KEY = "blankAccessKey"
    }

}

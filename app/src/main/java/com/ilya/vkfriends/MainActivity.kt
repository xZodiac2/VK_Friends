package com.ilya.vkfriends

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
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
import com.ilya.vkfriends.navigation.eventHandlers.FriendsScreenNavEventHandler
import com.ilya.vkfriends.navigation.eventHandlers.PhotosPreviewNavEventHandler
import com.ilya.vkfriends.navigation.eventHandlers.PhotosScreenNavEventHandler
import com.ilya.vkfriends.navigation.eventHandlers.ProfileScreenNavEventHandler
import com.ilya.vkfriends.navigation.eventHandlers.ProfileScreenNavEventHandler.Companion.BLANK_ACCESS_KEY
import com.ilya.vkfriends.navigation.eventHandlers.SearchScreenNavEventHandler
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
        enableEdgeToEdge()
        setContent {
            VkFriendsAppTheme {
                val navController = rememberNavController()

                var bottomBarVisible by remember { mutableStateOf(false) }

                Scaffold(
                    modifier = Modifier.safeDrawingPadding(),
                    bottomBar = {
                        if (bottomBarVisible) {
                            BottomBar(navController)
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
        val startDestination = when (accessTokenManager.accessToken) {
            null -> Destination.AuthScreen
            else -> Destination.FriendsScreen
        }

        NavHost(
            modifier = Modifier.padding(paddingValues),
            navController = navController,
            startDestination = startDestination
        ) {
            composable<Destination.AuthScreen>(
                enterTransition = { fadeIn(tween(0)) },
                exitTransition = { fadeOut(tween(0)) }
            ) {
                AuthorizationScreen {
                    navController.navigate(Destination.FriendsScreen) {
                        popUpTo(Destination.AuthScreen) { inclusive = true }
                    }
                }
            }
            composable<Destination.FriendsScreen>(
                enterTransition = { fadeIn(tween(0)) },
                exitTransition = { fadeOut(tween(0)) }
            ) {
                val eventHandler = FriendsScreenNavEventHandler(navController)

                FriendsScreen(
                    onExitConfirm = ::finish,
                    handleNavEvent = eventHandler::handleEvent
                )
                LaunchedEffect(Unit) { showBottomBar() }
            }
            composable<Destination.ProfileScreen>(
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left) },
                popEnterTransition = { fadeIn(tween(0)) },
                exitTransition = { fadeOut(tween(0)) }
            ) { backStackEntry ->
                val route = backStackEntry.toRoute<Destination.ProfileScreen>()
                val eventHandler = ProfileScreenNavEventHandler(navController)

                ProfileScreen(
                    userId = route.userId,
                    isPrivate = route.isPrivate,
                    handleNavEvent = eventHandler::handleEvent
                )
                LaunchedEffect(Unit) { showBottomBar() }
            }
            composable<Destination.SearchScreen>(
                enterTransition = { fadeIn(tween(0)) },
                exitTransition = { fadeOut(tween(0)) }
            ) {
                val eventHandler = SearchScreenNavEventHandler(navController)
                SearchScreen(eventHandler::handleEvent)
                LaunchedEffect(Unit) { showBottomBar() }
            }
            composable<Destination.PhotosPreview>(
                enterTransition = { fadeIn(tween(0)) },
                exitTransition = { fadeOut(tween(0)) }
            ) {
                val route = it.toRoute<Destination.PhotosPreview>()
                val eventHandler = PhotosPreviewNavEventHandler(navController)

                PhotosPreview(
                    userId = route.userId,
                    targetPhotoIndex = route.targetPhotoIndex,
                    photoIds = route.photoIds.fromIdsString(),
                    handleNavEvent = eventHandler::handleEvent
                )
                LaunchedEffect(Unit) { hideBottomBar() }
            }
            composable<Destination.PhotosScreen>(
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left) },
                popEnterTransition = { fadeIn(tween(0)) },
                exitTransition = { fadeOut(tween(0)) }
            ) {
                val route = it.toRoute<Destination.PhotosScreen>()
                val eventHandler = PhotosScreenNavEventHandler(navController)

                PhotosScreen(route.userId, eventHandler::handleEvent)
            }
            composable<Destination.VideoPreview>(
                enterTransition = { fadeIn(tween(0)) },
                exitTransition = { fadeOut(tween(0)) }
            ) {
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


}

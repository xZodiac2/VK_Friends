package com.ilya.vkfriends

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ilya.auth.screen.AuthorizationScreen
import com.ilya.core.appCommon.accessToken.AccessTokenManager
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
        setContent {
            VkFriendsAppTheme {
                val systemUiController = rememberSystemUiController()

                val navController = rememberNavController()
                val currentBackStackEntry = navController.currentBackStackEntryAsState()

                setSystemBarsColor(currentBackStackEntry, systemUiController)
                setSystemBarsVisibility(currentBackStackEntry, systemUiController)

                Scaffold(
                    bottomBar = {
                        val isBottomBarVisible = isBottomBarVisible(currentBackStackEntry)

                        if (isBottomBarVisible) {
                            BottomBar(navController, currentBackStackEntry.value)
                        }
                    },
                    containerColor = LocalColorScheme.current.primary
                ) { paddingValues ->
                    Navigation(
                        navController = navController,
                        paddingValues = paddingValues,
                    )
                }
            }
        }
    }

    @SuppressLint("ComposableNaming")
    @Composable
    @NonRestartableComposable
    private fun setSystemBarsVisibility(
        backStackEntry: State<NavBackStackEntry?>,
        systemUiController: SystemUiController
    ) {
        val itIsPhotosPreview by remember {
            derivedStateOf { backStackEntry.value?.destination?.hasRoute(Destination.PhotosPreview::class) == true }
        }
        val itIsVideoPreview by remember {
            derivedStateOf { backStackEntry.value?.destination?.hasRoute(Destination.VideoPreview::class) == true }
        }

        systemUiController.isSystemBarsVisible = !(itIsVideoPreview || itIsPhotosPreview)
    }

    @SuppressLint("ComposableNaming")
    @Composable
    @NonRestartableComposable
    private fun setSystemBarsColor(backStackEntry: State<NavBackStackEntry?>, systemUiController: SystemUiController) {
        val itIsAuthScreen by remember {
            derivedStateOf {
                backStackEntry.value?.destination?.hasRoute(Destination.AuthScreen::class) == true
            }
        }
        val itIsFriendsScreen by remember {
            derivedStateOf {
                backStackEntry.value?.destination?.hasRoute(Destination.FriendsScreen::class) == true
            }
        }

        if (itIsAuthScreen) systemUiController.setSystemBarsColor(LocalColorScheme.current.background)
        if (itIsFriendsScreen) systemUiController.setSystemBarsColor(LocalColorScheme.current.secondary)
    }

    @Composable
    @NonRestartableComposable
    private fun isBottomBarVisible(backStackEntry: State<NavBackStackEntry?>): Boolean {
        val itIsNotPhotosPreview by remember {
            derivedStateOf { backStackEntry.value?.destination?.hasRoute(Destination.PhotosPreview::class) == false }
        }
        val itIsNotVideoPreview by remember {
            derivedStateOf { backStackEntry.value?.destination?.hasRoute(Destination.VideoPreview::class) == false }
        }
        val itIsNotAuthScreen by remember {
            derivedStateOf { backStackEntry.value?.destination?.hasRoute(Destination.AuthScreen::class) == false }
        }
        return itIsNotVideoPreview && itIsNotPhotosPreview && itIsNotAuthScreen
    }

    @Composable
    private fun BottomBar(navController: NavController, currentBackStackEntry: NavBackStackEntry?) {
        val currentDestination = currentBackStackEntry?.destination
        val navigationBarItems = listOf(NavigationBarItem.FriendsView, NavigationBarItem.Search)

        NavigationBar(
            containerColor = LocalColorScheme.current.secondary,
            modifier = Modifier.border(1.dp, LocalColorScheme.current.secondary)
        ) {
            navigationBarItems.forEach { item ->
                NavigationBarItem(
                    selected = currentDestination?.hierarchy?.any {
                        currentBackStackEntry.lastDestinationName == item.destination::class.simpleName
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
    private fun Navigation(navController: NavHostController, paddingValues: PaddingValues) {
        val startDestination = getStartDestination()

        NavHost(
            modifier = Modifier.padding(paddingValues),
            navController = navController,
            startDestination = startDestination
        ) {
            authScreenComposable(navController)
            friendsScreenComposable(navController)
            profileScreenComposable(navController)
            searchScreenComposable(navController)
            photosPreviewComposable(navController)
            photosScreenComposable(navController)
            videoPreviewComposable(navController)
        }
    }

    private fun getStartDestination(): Destination {
        return when (accessTokenManager.accessToken) {
            null -> Destination.AuthScreen
            else -> Destination.FriendsScreen
        }
    }

    private fun NavGraphBuilder.authScreenComposable(navController: NavHostController) {
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
    }

    private fun NavGraphBuilder.friendsScreenComposable(navController: NavHostController) {
        composable<Destination.FriendsScreen>(
            enterTransition = { fadeIn(tween(0)) },
            exitTransition = { fadeOut(tween(0)) }
        ) {
            val eventHandler = remember { FriendsScreenNavEventHandler(navController) }

            FriendsScreen(
                onExitConfirm = ::finish,
                handleNavEvent = eventHandler::handleEvent
            )
        }
    }

    private fun NavGraphBuilder.profileScreenComposable(navController: NavHostController) {
        composable<Destination.ProfileScreen>(
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left) },
            popEnterTransition = { fadeIn(tween(0)) },
            exitTransition = {
                slideOutHorizontally(
                    animationSpec = tween(600),
                    targetOffsetX = { -it / 2 }
                ) + fadeOut(tween(300))
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(600)
                ) + fadeOut(tween(300))
            },
        ) { backStackEntry ->
            val route = backStackEntry.toRoute<Destination.ProfileScreen>()
            val eventHandler = remember { ProfileScreenNavEventHandler(navController) }

            ProfileScreen(
                userId = route.userId,
                isPrivate = route.isPrivate,
                handleNavEvent = eventHandler::handleEvent
            )
        }
    }

    private fun NavGraphBuilder.searchScreenComposable(navController: NavHostController) {
        composable<Destination.SearchScreen>(
            enterTransition = { fadeIn(tween(0)) },
            exitTransition = { fadeOut(tween(0)) }
        ) {
            val eventHandler = remember { SearchScreenNavEventHandler(navController) }
            SearchScreen(eventHandler::handleEvent)
        }
    }

    private fun NavGraphBuilder.photosPreviewComposable(navController: NavHostController) {
        composable<Destination.PhotosPreview>(
            enterTransition = { fadeIn(tween(0)) },
            exitTransition = { fadeOut(tween(0)) }
        ) {
            val route = it.toRoute<Destination.PhotosPreview>()
            val eventHandler = remember { PhotosPreviewNavEventHandler(navController) }

            PhotosPreview(
                userId = route.userId,
                targetPhotoIndex = route.targetPhotoIndex,
                photoIds = route.photoIds.fromIdsString(),
                handleNavEvent = eventHandler::handleEvent
            )
        }
    }

    private fun NavGraphBuilder.photosScreenComposable(navController: NavHostController) {
        composable<Destination.PhotosScreen>(
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left) },
            popEnterTransition = { fadeIn(tween(0)) },
            exitTransition = { fadeOut(tween(0)) },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(600)
                ) + fadeOut(tween(300))
            }
        ) {
            val route = it.toRoute<Destination.PhotosScreen>()
            val eventHandler = remember { PhotosScreenNavEventHandler(navController) }

            PhotosScreen(route.userId, eventHandler::handleEvent)
        }
    }

    private fun NavGraphBuilder.videoPreviewComposable(navController: NavHostController) {
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

package com.ilya.profileview.profileScreen.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ilya.core.appCommon.compose.basicComposables.snackbar.SnackbarEventEffect
import com.ilya.profileview.R
import com.ilya.profileview.photosScreen.OnLoading
import com.ilya.profileview.profileScreen.ProfileScreenState
import com.ilya.profileview.profileScreen.ProfileScreenViewModel
import com.ilya.profileview.profileScreen.components.profileCommon.TopBar
import com.ilya.profileview.profileScreen.components.profileCommon.profileHeader.ProfileHeader
import com.ilya.profileview.profileScreen.screens.event.EventReceiver
import com.ilya.profileview.profileScreen.screens.event.ProfileScreenNavEvent
import com.ilya.theme.LocalColorScheme
import com.ilya.theme.LocalTypography

@Composable
internal fun PrivateProfile(
    viewModel: ProfileScreenViewModel,
    userId: Long,
    handleNavEvent: (ProfileScreenNavEvent) -> Unit
) {
    val screenState = viewModel.screenState.collectAsState()
    val snackbarState by viewModel.snackbarState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val eventReceiver = EventReceiver(viewModel)

    SnackbarEventEffect(
        state = snackbarState,
        onConsumed = eventReceiver::onSnackbarConsumed,
        action = { snackbarHostState.showSnackbar(it) }
    )

    Scaffold(
        containerColor = LocalColorScheme.current.primary,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopBar(
                onBackClick = eventReceiver::onBackClick,
                userId = userId,
                contentScrolled = false,
            )
        }
    ) { padding ->
        when (val stateValue = screenState.value) {
            ProfileScreenState.Loading -> OnLoading(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxHeight()
            )

            is ProfileScreenState.ViewData -> {
                Column(
                    modifier = Modifier.padding(padding),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProfileHeader(
                        user = stateValue.user,
                        eventReceiver = eventReceiver
                    )
                    PrivateProfileBanner()
                }
            }

            is ProfileScreenState.Error -> OnErrorState(
                errorType = stateValue.errorType,
                onEmptyAccessToken = eventReceiver::onEmptyAccessToken,
                onTryAgainClick = eventReceiver::onRetry,
                padding = padding
            )
        }
    }

    LaunchedEffect(Unit) {
        eventReceiver.onStart(userId)
        viewModel.navEventFlow.collect(handleNavEvent)
    }

}

@Composable
private fun PrivateProfileBanner() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = LocalColorScheme.current.cardContainerColor)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                modifier = Modifier
                    .fillMaxSize(0.15f)
                    .aspectRatio(1f),
                tint = LocalColorScheme.current.primaryTextColor,
                contentDescription = "privateProfile"
            )
            Column(
                modifier = Modifier.padding(start = 28.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.private_profile),
                    color = LocalColorScheme.current.primaryTextColor,
                    fontSize = LocalTypography.current.big
                )
                Text(
                    text = stringResource(R.string.add_user_to_friends_to_see_info),
                    color = LocalColorScheme.current.secondaryTextColor
                )
            }
        }
    }
}

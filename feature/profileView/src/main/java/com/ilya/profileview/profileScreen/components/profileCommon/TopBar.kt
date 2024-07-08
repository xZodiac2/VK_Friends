package com.ilya.profileview.profileScreen.components.profileCommon

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ilya.profileview.R
import com.ilya.theme.LocalColorScheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TopBar(
    onBackClick: () -> Unit,
    userId: Long,
    contentOffset: Float
) {
    val contentScrolled = contentOffset < -50f
    val animatedBackgroundColor = animateColorAsState(
        targetValue = if (contentScrolled) {
            LocalColorScheme.current.secondary
        } else {
            LocalColorScheme.current.cardContainerColor
        },
        label = "topBarBackground"
    )

    Box {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(
                        id = R.string.profile_screen_name,
                        "id$userId",
                    ),
                    color = LocalColorScheme.current.primaryTextColor
                )
            },
            navigationIcon = {
                IconButton(onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "back",
                        tint = LocalColorScheme.current.iconTintColor
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = animatedBackgroundColor.value
            )
        )
    }
}

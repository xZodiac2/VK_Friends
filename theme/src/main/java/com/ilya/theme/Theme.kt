package com.ilya.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun VkFriendsAppTheme(content: @Composable () -> Unit) {
    val colorScheme = if (isSystemInDarkTheme()) {
        ColorScheme(
            primary = DarkColorScheme.primary,
            secondary = DarkColorScheme.secondary,
            cardContainerColor = DarkColorScheme.cardContainerColor,
            background = DarkColorScheme.background,
            primaryTextColor = DarkColorScheme.primaryTextColor,
            secondaryTextColor = DarkColorScheme.secondaryTextColor,
            containerTextFieldColor = DarkColorScheme.containerTextFieldColor,
            valueTextFieldColor = DarkColorScheme.valueTextFieldColor,
            focusedIndicatorTextFieldColor = DarkColorScheme.focusedIndicatorTextFieldColor,
            unfocusedIndicatorTextFieldColor = DarkColorScheme.unfocusedIndicatorTextFieldColor,
            placeholderTextFieldColor = DarkColorScheme.placeholderTextFieldColor,
            buttonColor = DarkColorScheme.buttonColor,
            selectedIconColor = DarkColorScheme.selectedIconColor,
            unselectedIconColor = DarkColorScheme.unselectedIconColor,
            bottomNavSelectedIndicatorColor = DarkColorScheme.bottomNavSelectedIndicatorColor,
            iconTintColor = DarkColorScheme.iconTintColor,
            primaryIconTintColor = DarkColorScheme.primaryIconTintColor
        )
    } else {
        ColorScheme()
    }

    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(colorScheme.secondary)

    CompositionLocalProvider(
        LocalColorScheme provides colorScheme,
        LocalTypography provides Typography
    ) {
        Box(modifier = Modifier.background(colorScheme.primary)) { content() }
    }
}
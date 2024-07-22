package com.ilya.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

data class ColorScheme(
    val primary: Color = LightColorScheme.primary,
    val secondary: Color = LightColorScheme.secondary,
    val cardContainerColor: Color = LightColorScheme.cardContainerColor,
    val background: Color = LightColorScheme.background,
    val primaryTextColor: Color = LightColorScheme.primaryTextColor,
    val secondaryTextColor: Color = LightColorScheme.secondaryTextColor,
    val containerTextFieldColor: Color = LightColorScheme.containerTextFieldColor,
    val valueTextFieldColor: Color = LightColorScheme.valueTextFieldColor,
    val focusedIndicatorTextFieldColor: Color = LightColorScheme.focusedIndicatorTextFieldColor,
    val unfocusedIndicatorTextFieldColor: Color = LightColorScheme.unfocusedIndicatorTextFieldColor,
    val placeholderTextFieldColor: Color = LightColorScheme.placeholderTextFieldColor,
    val buttonColor: Color = LightColorScheme.buttonColor,
    val selectedIconColor: Color = LightColorScheme.primaryIconColor,
    val unselectedIconColor: Color = LightColorScheme.secondaryIconColor,
    val bottomNavSelectedIndicatorColor: Color = LightColorScheme.bottomNavSelectedIndicatorColor,
    val iconTintColor: Color = LightColorScheme.iconTintColor,
    val primaryIconTintColor: Color = LightColorScheme.primaryIconTintColor
)

internal object LightColorScheme {
    val primary = Color(243, 243, 243, 255)
    val secondary = Color(245, 245, 245, 255)

    val cardContainerColor = Color.White
    val background = Color(241, 241, 241, 255)

    val primaryTextColor = Color.Black
    val secondaryTextColor = Color(126, 126, 126)

    val containerTextFieldColor = Color.White
    val valueTextFieldColor = Color.Black
    val focusedIndicatorTextFieldColor = Color.Black
    val unfocusedIndicatorTextFieldColor = Color.Gray
    val placeholderTextFieldColor = Color.Black

    val buttonColor = Color(31, 127, 245, 255)

    val primaryIconColor = Color.Black
    val secondaryIconColor = Color(61, 61, 61, 255)

    val bottomNavSelectedIndicatorColor = Color(235, 235, 235, 255)

    val primaryIconTintColor = Color(31, 127, 245, 255)
    val iconTintColor = Color.Black
}

internal object DarkColorScheme {
    val primary = Color(24, 24, 24, 255)
    val secondary = Color(30, 30, 30, 255)

    val cardContainerColor = Color(34, 34, 34, 255)
    val background = Color(24, 24, 24, 255)

    val primaryTextColor = Color.White
    val secondaryTextColor = Color(170, 170, 170, 255)

    val containerTextFieldColor = cardContainerColor
    val valueTextFieldColor = Color.White
    val focusedIndicatorTextFieldColor = Color.White
    val unfocusedIndicatorTextFieldColor = Color.DarkGray
    val placeholderTextFieldColor = Color.LightGray

    val buttonColor = Color(77, 157, 255, 255)

    val selectedIconColor = Color.White
    val unselectedIconColor = Color(226, 226, 226, 255)

    val bottomNavSelectedIndicatorColor = Color(40, 40, 40, 255)

    val primaryIconTintColor = Color(77, 157, 255, 255)
    val iconTintColor = Color.White
}


val LocalColorScheme = compositionLocalOf { ColorScheme() }
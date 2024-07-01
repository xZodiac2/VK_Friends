package com.ilya.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.sp

object Typography {
    val tiny = 14.sp
    val small = 16.sp
    val average = 18.sp
    val big = 20.sp
    val large = 24.sp
    val enormous = 30.sp
}

val LocalTypography = compositionLocalOf { Typography }
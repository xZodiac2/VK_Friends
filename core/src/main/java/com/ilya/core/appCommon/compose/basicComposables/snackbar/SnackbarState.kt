package com.ilya.core.appCommon.compose.basicComposables.snackbar

import androidx.compose.runtime.Stable
import com.ilya.core.appCommon.StringResource

@Stable
sealed interface SnackbarState {
    data object Consumed : SnackbarState
    data class Triggered(val text: StringResource) : SnackbarState
}
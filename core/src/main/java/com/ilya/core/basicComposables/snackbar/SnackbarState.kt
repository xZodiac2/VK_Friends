package com.ilya.core.basicComposables.snackbar

import com.ilya.core.appCommon.StringResource

sealed interface SnackbarState {
    object Consumed : SnackbarState
    data class Triggered(val text: StringResource) : SnackbarState
}
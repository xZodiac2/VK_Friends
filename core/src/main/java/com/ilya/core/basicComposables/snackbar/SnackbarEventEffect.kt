package com.ilya.core.basicComposables.snackbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.NonRestartableComposable
import com.ilya.core.appCommon.resolve

@Composable
@NonRestartableComposable
fun SnackbarEventEffect(
    state: SnackbarState,
    onConsumed: () -> Unit,
    action: suspend (String) -> Unit
) {
    if (state is SnackbarState.Triggered) {
        val text = state.text.resolve()
        LaunchedEffect(key1 = Unit) {
            action(text)
            onConsumed()
        }
    }
}
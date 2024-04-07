package com.ilya.core.basicComposables.alertDialog

import androidx.compose.runtime.Composable
import com.ilya.core.appCommon.StringResource

interface AlertDialogState {

    object Consumed : AlertDialogState

    data class Triggered(
        val title: StringResource? = null,
        val text: StringResource? = null,
        val onConfirm: () -> Unit,
        val onDismiss: () -> Unit
    ) : AlertDialogState

}

@Composable
fun AlertDialogStateHandler(state: AlertDialogState) {
    when (state) {
        AlertDialogState.Consumed -> Unit
        is AlertDialogState.Triggered -> BaseAlertDialog(state = state)
    }
}
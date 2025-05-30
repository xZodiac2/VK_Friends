package com.ilya.core.appCommon.compose.basicComposables.alertDialog

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ilya.core.R
import com.ilya.core.appCommon.compose.basicComposables.BaseButton
import com.ilya.core.appCommon.compose.basicComposables.BaseButtonStyles
import com.ilya.core.appCommon.resolve
import com.ilya.theme.LocalColorScheme
import com.ilya.theme.LocalTypography

@Composable
fun BaseAlertDialog(state: AlertDialogState.Triggered) {
  AlertDialog(
    icon = {
      Icon(
        modifier = Modifier.size(50.dp),
        painter = painterResource(id = R.drawable.warning_icon),
        contentDescription = "warning",
        tint = LocalColorScheme.current.selectedIconColor
      )
    },
    onDismissRequest = state.onDismiss,
    confirmButton = {
      BaseButton(onClick = state.onConfirm) {
        Text(text = stringResource(id = R.string.confirm))
      }
    },
    dismissButton = {
      BaseButton(
        onClick = state.onDismiss,
        style = BaseButtonStyles.Unattractive
      ) {
        Text(text = stringResource(id = R.string.dismiss))
      }
    },
    containerColor = LocalColorScheme.current.primary,
    title = {
      if (state.title != null) {
        Text(
          modifier = Modifier.fillMaxWidth(),
          text = state.title.resolve(),
          textAlign = TextAlign.Center,
          color = LocalColorScheme.current.primaryTextColor
        )
      }
    },
    text = {
      if (state.text != null) {
        Text(
          modifier = Modifier.fillMaxWidth(),
          text = state.text.resolve(),
          textAlign = TextAlign.Center,
          color = LocalColorScheme.current.secondaryTextColor,
          fontSize = LocalTypography.current.average
        )
      }
    }
  )
}
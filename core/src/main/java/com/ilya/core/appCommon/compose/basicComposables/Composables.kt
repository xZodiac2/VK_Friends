package com.ilya.core.appCommon.compose.basicComposables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ilya.core.appCommon.StringResource
import com.ilya.core.appCommon.resolve
import com.ilya.theme.LocalColorScheme

@Composable
fun BaseButton(
  modifier: Modifier = Modifier,
  onClick: () -> Unit,
  enabled: Boolean = true,
  elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
  style: BaseButtonStyles = BaseButtonStyles.Attractive,
  contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
  content: @Composable RowScope.() -> Unit,
) {
  Button(
    onClick,
    modifier,
    enabled,
    shape = RoundedCornerShape(8.dp),
    colors = style.resolveColors(),
    elevation,
    border = style.resolveBorder(),
    contentPadding,
    interactionSource,
    content
  )
}

sealed interface BaseButtonStyles {
  data object Attractive : BaseButtonStyles
  data object Unattractive : BaseButtonStyles
}

@Composable
private fun BaseButtonStyles.resolveColors(): ButtonColors = with(ButtonDefaults) {
  return when (this@resolveColors) {
    BaseButtonStyles.Attractive -> buttonColors(containerColor = LocalColorScheme.current.buttonColor)
    BaseButtonStyles.Unattractive -> buttonColors(
      containerColor = LocalColorScheme.current.primary,
      contentColor = LocalColorScheme.current.buttonColor
    )
  }
}

@Composable
private fun BaseButtonStyles.resolveBorder(): BorderStroke? {
  return when (this) {
    BaseButtonStyles.Attractive -> null
    BaseButtonStyles.Unattractive -> BorderStroke(
      width = 2.dp,
      color = LocalColorScheme.current.buttonColor
    )
  }
}

@Composable
fun OnError(
  modifier: Modifier = Modifier,
  message: StringResource,
  buttonText: StringResource,
  onButtonClick: () -> Unit
) {
  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = message.resolve(),
      textAlign = TextAlign.Center,
      color = LocalColorScheme.current.primaryTextColor
    )
    BaseButton(onClick = onButtonClick) {
      Text(text = buttonText.resolve())
    }
  }
}

package com.ilya.core.basicComposables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ilya.core.appCommon.StringResource
import com.ilya.core.appCommon.resolve
import com.ilya.theme.LocalColorScheme
import com.ilya.theme.LocalTypography

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
    object Attractive : BaseButtonStyles
    object Unattractive : BaseButtonStyles
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
            width = 1.dp,
            color = LocalColorScheme.current.buttonColor
        )
    }
}


@Composable
fun UserCard(
    modifier: Modifier = Modifier,
    onCardClick: (Long) -> Unit,
    id: Long,
    photoUrl: String,
    firstName: String,
    lastName: String
) {
    Card(
        modifier = Modifier.clickable { onCardClick(id) },
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = LocalColorScheme.current.cardContainerColor)
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            AsyncImage(
                model = photoUrl,
                contentDescription = "user_photo",
                modifier = Modifier
                    .clip(CircleShape)
                    .size(100.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                text = "$firstName $lastName",
                fontSize = LocalTypography.current.average,
                modifier = Modifier.padding(vertical = 8.dp),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                color = LocalColorScheme.current.primaryTextColor
            )
        }
    }
}

@Composable
fun OnError(
    modifier: Modifier = Modifier,
    message: StringResource,
    buttonText: StringResource,
    onTryAgainClick: () -> Unit
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
        BaseButton(onClick = onTryAgainClick) {
            Text(text = buttonText.resolve())
        }
    }
}

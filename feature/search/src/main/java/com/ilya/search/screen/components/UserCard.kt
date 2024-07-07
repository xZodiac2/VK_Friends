package com.ilya.search.screen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ilya.data.paging.User
import com.ilya.theme.LocalColorScheme
import com.ilya.theme.LocalTypography

@Composable
internal fun UserCard(
    modifier: Modifier = Modifier,
    onCardClick: (Long) -> Unit,
    user: User
) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable { onCardClick(user.id) },
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = LocalColorScheme.current.cardContainerColor)
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize(0.7f)
                    .clip(CircleShape)
                    .aspectRatio(1f),
                model = user.photoUrl,
                contentDescription = "user_photo",
                contentScale = ContentScale.Crop
            )
            Text(
                text = "${user.firstName} ${user.lastName}",
                fontSize = LocalTypography.current.small,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                color = LocalColorScheme.current.primaryTextColor,
                textAlign = TextAlign.Center
            )
        }

    }
}
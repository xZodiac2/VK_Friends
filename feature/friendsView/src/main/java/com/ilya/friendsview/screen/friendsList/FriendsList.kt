package com.ilya.friendsview.screen.friendsList

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ilya.core.basicComposables.BaseButton
import com.ilya.data.User
import com.ilya.friendsview.R
import com.ilya.theme.LocalColorScheme
import com.ilya.theme.LocalTypography

@Composable
fun FriendsList(users: List<User>, onProfileViewButtonClick: (Long) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(users) { user ->
            UserCard(user = user, onButtonPress = onProfileViewButtonClick)
        }
    }
}

@Composable
fun UserCard(modifier: Modifier = Modifier, user: User, onButtonPress: (Long) -> Unit) {
    Card(
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = LocalColorScheme.current.cardContainerColor)
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            AsyncImage(
                model = user.photo_200_orig,
                contentDescription = "user_photo",
                modifier = Modifier
                    .clip(CircleShape)
                    .size(80.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                text = "${user.first_name} ${user.last_name}",
                fontSize = LocalTypography.current.lowFontSize,
                modifier = Modifier.padding(vertical = 8.dp),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                color = LocalColorScheme.current.primaryTextColor
            )
            BaseButton(
                onClick = { onButtonPress(user.id) },
            ) {
                Text(
                    text = stringResource(id = R.string.view_profile),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
    
}

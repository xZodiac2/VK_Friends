package com.ilya.profileview.presentation.profileScreen.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ilya.core.appCommon.enums.FriendStatus
import com.ilya.core.appCommon.enums.Relation
import com.ilya.core.appCommon.enums.Sex
import com.ilya.core.basicComposables.BaseButton
import com.ilya.profileViewDomain.models.User
import com.ilya.profileview.R
import com.ilya.theme.LocalColorScheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProfileHeader(user: User, friendRequest: (User) -> Unit) {
    var showSheet by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = LocalColorScheme.current.cardContainerColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .size(140.dp)
                    .clip(CircleShape),
                model = user.photoUrl,
                contentDescription = "avatar",
                contentScale = ContentScale.Crop
            )
            Text(
                text = "${user.firstName} ${user.lastName}",
                fontSize = 28.sp,
                modifier = Modifier.padding(top = 8.dp),
                color = LocalColorScheme.current.primaryTextColor
            )
            if (user.status.isNotBlank()) {
                Text(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .widthIn(max = 350.dp),
                    text = user.status,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                    color = LocalColorScheme.current.primaryTextColor
                )
            }
            TextButton(onClick = { showSheet = true }) {
                Row {
                    Icon(
                        modifier = Modifier
                            .padding(top = 2.dp, end = 2.dp)
                            .size(16.dp),
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "more",
                        tint = LocalColorScheme.current.iconTintColor
                    )
                    Text(
                        text = stringResource(id = R.string.details),
                        color = LocalColorScheme.current.primaryTextColor,
                        fontWeight = FontWeight.W400,
                    )
                }
            }
            if (!user.isAccountOwner) {
                BaseButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp),
                    onClick = { friendRequest(user) }
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(20.dp),
                        painter = when (user.friendStatus) {
                            FriendStatus.NOT_FRIENDS -> painterResource(id = R.drawable.plus)
                            FriendStatus.WAITING -> painterResource(id = R.drawable.clock)
                            FriendStatus.FRIENDS -> painterResource(id = R.drawable.crossed_out_human)
                            FriendStatus.SUBSCRIBED -> painterResource(id = R.drawable.plus)
                        },
                        contentDescription = "add",
                        tint = Color.White
                    )
                    Text(
                        text = stringResource(
                            id = when (user.friendStatus) {
                                FriendStatus.NOT_FRIENDS -> R.string.status_not_friend
                                FriendStatus.FRIENDS -> R.string.status_friends
                                FriendStatus.WAITING -> R.string.status_waiting_for_response
                                FriendStatus.SUBSCRIBED -> R.string.status_friend_request
                            }
                        ),
                        fontSize = 18.sp
                    )
                }
            }
        }
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            containerColor = LocalColorScheme.current.cardContainerColor,
            dragHandle = { BottomSheetDefaults.DragHandle(color = LocalColorScheme.current.iconTintColor) }
        ) { SheetContent(user = user) }
    }
}

@Composable
private fun SheetContent(user: User) {
    Column(
        modifier = Modifier
            .padding(start = 20.dp, bottom = 20.dp, end = 20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(id = R.string.details),
            fontSize = 32.sp,
            color = LocalColorScheme.current.primaryTextColor
        )
        Spacer(modifier = Modifier)
        if (user.status.isNotBlank()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(20.dp),
                    imageVector = Icons.Outlined.Info,
                    contentDescription = "status",
                    tint = LocalColorScheme.current.iconTintColor
                )
                Text(
                    text = user.status,
                    color = LocalColorScheme.current.primaryTextColor
                )
            }
        }
        if (user.birthday.isNotBlank()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(20.dp),
                    imageVector = Icons.Outlined.DateRange,
                    contentDescription = "bdate",
                    tint = LocalColorScheme.current.iconTintColor
                )
                Text(
                    text = stringResource(id = R.string.birthday_date, user.birthday),
                    color = LocalColorScheme.current.iconTintColor
                )
            }
        }
        if (user.relation != Relation.NOT_STATED) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(20.dp),
                    imageVector = Icons.Outlined.FavoriteBorder,
                    contentDescription = "relation",
                    tint = LocalColorScheme.current.iconTintColor
                )
                val text = when (user.relation) {
                    Relation.NOT_MARRIED -> stringResource(
                        id = when (user.sex) {
                            Sex.WOMAN -> R.string.not_married_woman
                            else -> R.string.not_married
                        }
                    )

                    Relation.HAVE_FRIEND -> stringResource(id = R.string.have_friend)
                    Relation.ENGAGED -> calculateRelationString(
                        user = user,
                        manString = R.string.engaged,
                        womanString = R.string.engaged_woman,
                        manStringExtended = R.string.engaged_extended,
                        womanStringExtended = R.string.engaged_woman_extended
                    )

                    Relation.MARRIED -> calculateRelationString(
                        user = user,
                        manString = R.string.married,
                        womanString = R.string.married_woman,
                        manStringExtended = R.string.married_extended,
                        womanStringExtended = R.string.married_woman_extended
                    )

                    Relation.ACTIVELY_LOOK_FOR -> stringResource(id = R.string.looking_for_seomeone)
                    Relation.ALL_IS_TOUGH -> stringResource(id = R.string.all_is_tough)
                    Relation.IN_LOVE -> calculateRelationString(
                        user = user,
                        manString = R.string.in_love,
                        womanString = R.string.in_love_women,
                        manStringExtended = R.string.in_love_extended,
                        womanStringExtended = R.string.in_love_woman_extended
                    )

                    Relation.IN_CIVIL_MARRIAGE -> calculateRelationString(
                        user = user,
                        manString = R.string.civil_marriage,
                        womanString = R.string.civil_marriage,
                        manStringExtended = R.string.civil_marriage_extended,
                        womanStringExtended = R.string.civil_marriage_extended
                    )

                    else -> null
                }
                text?.let {
                    Text(
                        text = it,
                        color = LocalColorScheme.current.primaryTextColor
                    )
                }
            }
        }
        user.counters?.let { counters ->
            counters.followers?.let { followers ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(20.dp),
                        imageVector = Icons.Outlined.AccountCircle,
                        contentDescription = "subscribers",
                        tint = LocalColorScheme.current.iconTintColor
                    )
                    Text(
                        text = pluralStringResource(
                            id = R.plurals.subscribers,
                            count = followers,
                            followers
                        ),
                        color = LocalColorScheme.current.primaryTextColor
                    )
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            counters.friends?.let { friends ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .size(36.dp),
                            imageVector = Icons.Outlined.AccountBox,
                            contentDescription = "friends",
                            tint = LocalColorScheme.current.primaryIconTintColor
                        )
                        Text(
                            text = stringResource(id = R.string.firends),
                            fontSize = 20.sp,
                            color = LocalColorScheme.current.primaryTextColor
                        )
                    }
                    Text(
                        text = friends.toString(),
                        fontSize = 20.sp,
                        color = LocalColorScheme.current.primaryTextColor
                    )
                }

            }
            counters.subscriptions?.let { subscriptions ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .size(36.dp),
                            painter = painterResource(id = R.drawable.subscribers),
                            contentDescription = "friends",
                            tint = LocalColorScheme.current.primaryIconTintColor
                        )
                        Text(
                            text = stringResource(id = R.string.subscriptions),
                            fontSize = 20.sp,
                            color = LocalColorScheme.current.primaryTextColor
                        )
                    }
                    Text(
                        text = subscriptions.toString(),
                        fontSize = 20.sp,
                        color = LocalColorScheme.current.primaryTextColor
                    )
                }
            }
        }
        user.city?.let { city ->
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            Column {
                Text(
                    text = stringResource(id = R.string.hometown),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W400,
                    color = LocalColorScheme.current.secondaryTextColor
                )
                Text(
                    text = city.name,
                    fontSize = 18.sp,
                    color = LocalColorScheme.current.primaryTextColor
                )
            }
        }
    }
}

@Composable
private fun calculateRelationString(
    user: User,
    @StringRes manString: Int,
    @StringRes womanString: Int,
    @StringRes manStringExtended: Int,
    @StringRes womanStringExtended: Int
): String {
    return stringResource(
        id = when (user.sex) {
            Sex.WOMAN -> when (user.partnerExtended) {
                null -> womanString
                else -> womanStringExtended
            }

            else -> when (user.partnerExtended) {
                null -> manString
                else -> manStringExtended
            }
        },
        user.partnerExtended?.let {
            "${it.firstName} ${it.lastName}"
        } ?: ""
    )
}
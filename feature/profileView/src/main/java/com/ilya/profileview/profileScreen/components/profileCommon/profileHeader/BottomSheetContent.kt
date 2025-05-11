package com.ilya.profileview.profileScreen.components.profileCommon.profileHeader

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ilya.core.appCommon.enums.Relation
import com.ilya.core.appCommon.enums.Sex
import com.ilya.profileViewDomain.City
import com.ilya.profileViewDomain.Counters
import com.ilya.profileViewDomain.User
import com.ilya.profileview.R
import com.ilya.theme.LocalColorScheme

@Composable
internal fun SheetContent(user: User) {
  Column(
    modifier = Modifier
      .padding(start = 20.dp, bottom = 20.dp, end = 20.dp)
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    Header()
    Status(user.status)
    Birthday(user.birthday)
    Relations(user)
    Counters(user.counters)
    City(user.city)
  }
}

@Composable
private fun Header() {
  Text(
    text = stringResource(id = R.string.details),
    fontSize = 32.sp,
    color = LocalColorScheme.current.primaryTextColor
  )
  Spacer(modifier = Modifier)
}

@Composable
private fun Status(status: String) {
  if (status.isNotBlank()) {
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
        text = status,
        color = LocalColorScheme.current.primaryTextColor
      )
    }
  }
}

@Composable
private fun Birthday(birthday: String) {
  if (birthday.isNotBlank()) {
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
        text = stringResource(id = R.string.birthday_date, birthday),
        color = LocalColorScheme.current.iconTintColor
      )
    }
  }
}

@Composable
private fun Relations(user: User) {
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
}

@Composable
private fun Counters(counters: Counters?) {
  counters?.let {
    Followers(counters.followers)
    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
    Friends(counters.friends)
    Subscriptions(counters.subscriptions)
  }
}

@Composable
private fun Followers(followers: Int?) {
  followers?.let {
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
}

@Composable
private fun Friends(friends: Int?) {
  friends?.let {
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
}

@Composable
private fun Subscriptions(subscriptions: Int?) {
  subscriptions?.let {
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

@Composable
private fun City(city: City?) {
  city?.let {
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
      Sex.WOMAN -> when (user.partner) {
        null -> womanString
        else -> womanStringExtended
      }

      else -> when (user.partner) {
        null -> manString
        else -> manStringExtended
      }
    },
    user.partner?.let {
      "${it.firstName} ${it.lastName}"
    } ?: ""
  )
}
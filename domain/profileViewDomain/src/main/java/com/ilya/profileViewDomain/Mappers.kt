package com.ilya.profileViewDomain

import com.ilya.core.appCommon.enums.FriendStatus
import com.ilya.core.appCommon.enums.Relation
import com.ilya.core.appCommon.enums.Sex
import com.ilya.data.retrofit.api.dto.CityDto
import com.ilya.data.retrofit.api.dto.CountersDto
import com.ilya.data.retrofit.api.dto.PartnerDto
import com.ilya.data.retrofit.api.dto.PhotoDto
import com.ilya.data.retrofit.api.dto.UserDto
import com.ilya.paging.mappers.toPhoto

fun UserDto.toUser(photos: List<PhotoDto>): User {
  return User(
    id = id,
    firstName = firstName,
    lastName = lastName,
    photoUrl = photoUrl,
    friendStatus = FriendStatus.entries.find { it.value == friendStatus }
      ?: FriendStatus.NOT_FRIENDS,
    birthday = birthday,
    status = status,
    city = city?.toCity(),
    relation = Relation.entries.find { it.value == relation } ?: Relation.NOT_STATED,
    partner = partner?.toPartner(),
    sex = Sex.entries.find { it.value == sex } ?: Sex.NOT_STATED,
    counters = counters?.toCounters(),
    photos = photos.map { it.toPhoto() }
  )
}


private fun CountersDto.toCounters(): Counters {
  return Counters(
    friends = friends,
    subscriptions = subscriptions,
    followers = followers
  )
}

fun PartnerDto.toPartner(): Partner {
  return Partner(
    id = id,
    firstName = firstName,
    lastName = lastName
  )
}

private fun CityDto.toCity(): City {
  return City(
    name = name,
    id = id
  )
}
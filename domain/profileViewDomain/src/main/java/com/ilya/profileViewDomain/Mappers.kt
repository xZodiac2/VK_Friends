package com.ilya.profileViewDomain

import com.ilya.core.appCommon.enums.FriendStatus
import com.ilya.core.appCommon.enums.Relation
import com.ilya.core.appCommon.enums.Sex
import com.ilya.data.network.retrofit.api.CityDto
import com.ilya.data.network.retrofit.api.CountersDto
import com.ilya.data.network.retrofit.api.PartnerDto
import com.ilya.data.network.retrofit.api.UserDto

fun UserDto.toUser(): User {
    return User(
        id = id,
        firstName = firstName,
        lastName = lastName,
        photoUrl = photoUrl,
        friendStatus = FriendStatus.values().find { it.value == friendStatus }
            ?: FriendStatus.NOT_FRIENDS,
        birthday = birthday,
        status = status,
        city = city?.toCity(),
        relation = Relation.values().find { it.value == relation } ?: Relation.NOT_STATED,
        partner = partner?.toPartner(),
        sex = Sex.values().find { it.value == sex } ?: Sex.NOT_STATED,
        counters = counters?.toCounters()
    )
}

fun CountersDto.toCounters(): Counters {
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

fun CityDto.toCity(): City {
    return City(
        name = name,
        id = id
    )
}
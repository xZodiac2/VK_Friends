package com.ilya.profileViewDomain

import com.ilya.core.appCommon.enums.FriendStatus
import com.ilya.core.appCommon.enums.Relation
import com.ilya.core.appCommon.enums.Sex

data class User(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val photoUrl: String,
    val friendStatus: FriendStatus,
    val birthday: String,
    val status: String,
    val city: City?,
    val relation: Relation,
    val partner: Partner?,
    val partnerExtended: User? = null,
    val isAccountOwner: Boolean = false,
    val sex: Sex,
    val counters: Counters?
)

data class Partner(
    val id: Long,
    val firstName: String,
    val lastName: String
)

data class City(
    val name: String,
    val id: Int
)

data class Counters(
    val friends: Int?,
    val followers: Int?,
    val subscriptions: Int?
)
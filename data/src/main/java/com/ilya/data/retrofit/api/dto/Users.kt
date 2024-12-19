package com.ilya.data.retrofit.api.dto

import com.squareup.moshi.Json


data class UserDataResponse(
    @Json(name = "response") val response: List<UserDto>
)


data class UserExtendedResponse(
    @Json(name = "response") val response: UserExtendedResponseData
)

data class UserExtendedResponseData(
    @Json(name = "user") val user: UserDto,
    @Json(name = "partner") val partner: PartnerDto?,
    @Json(name = "photos") val photos: List<PhotoDto>?
)

data class GetUsersResponse(
    @Json(name = "response") val responseData: GetUsersResponseData,
)

data class GetUsersResponseData(
    @Json(name = "count") val count: Int,
    @Json(name = "items") val items: List<UserDto>,
)

data class UserDto(
    @Json(name = "id") val id: Long,
    @Json(name = "first_name") val firstName: String,
    @Json(name = "last_name") val lastName: String,
    @Json(name = "sex") val sex: Int = 0,
    @Json(name = "photo_200_orig") val photoUrl: String = "",
    @Json(name = "bdate") val birthday: String = "",
    @Json(name = "status") val status: String = "",
    @Json(name = "city") val city: CityDto? = null,
    @Json(name = "relation") val relation: Int = 0,
    @Json(name = "relation_partner") val partner: PartnerDto? = null,
    @Json(name = "friend_status") val friendStatus: Int = 0,
    @Json(name = "counters") val counters: CountersDto? = null,
    @Json(name = "is_closed") val isClosed: Boolean = true
)

data class PartnerDto(
    @Json(name = "id") val id: Long,
    @Json(name = "first_name") val firstName: String,
    @Json(name = "last_name") val lastName: String
)

data class CityDto(
    @Json(name = "title") val name: String,
    @Json(name = "id") val id: Int
)

data class CountersDto(
    @Json(name = "friends") val friends: Int? = null,
    @Json(name = "followers") val followers: Int? = null,
    @Json(name = "subscriptions") val subscriptions: Int? = null
)
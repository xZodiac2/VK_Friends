package com.ilya.data.network.retrofit.api

import com.squareup.moshi.Json


data class Response(
    @Json(name = "response") val responseData: ResponseData,
)

data class ResponseData(
    @Json(name = "count") val count: Int,
    @Json(name = "items") val items: List<UserDto>,
)

data class UserDto(
    @Json(name = "id") val id: Long = 0,
    @Json(name = "first_name") val firstName: String = "",
    @Json(name = "last_name") val lastName: String = "",
    @Json(name = "sex") val sex: Int = 0,
    @Json(name = "photo_200_orig") val photoUrl: String = "",
    @Json(name = "bdate") val birthday: String = "",
    @Json(name = "status") val status: String = "",
    @Json(name = "city") val city: CityDto? = null,
    @Json(name = "relation") val relation: Int = 0,
    @Json(name = "relation_partner") val partner: PartnerDto? = null,
    @Json(name = "friend_status") val friendStatus: Int = 0,
    @Json(name = "counters") val counters: CountersDto? = null
)

data class PartnerDto(
    @Json(name = "id") val id: Long = 0,
    @Json(name = "first_name") val firstName: String = "",
    @Json(name = "last_name") val lastName: String = ""
)

data class CityDto(
    @Json(name = "title") val name: String = "",
    @Json(name = "id") val id: Int = 0
)

data class CountersDto(
    @Json(name = "friends") val friends: Int? = null,
    @Json(name = "followers") val followers: Int? = null,
    @Json(name = "subscriptions") val subscriptions: Int? = null
)


data class UserDataResponse(
    @Json(name = "response") val response: List<UserDto>
)


data class WallResponse(
    @Json(name = "response") val response: WallResponseData
)

data class WallResponseData(
    @Json(name = "count") val count: Int,
    @Json(name = "items") val items: List<PostDto>
)

data class PostDto(
    @Json(name = "id") val id: Long,
    @Json(name = "text") val text: String = "",
    @Json(name = "attachments") val attachments: List<AttachmentDto>,
    @Json(name = "likes") val likes: LikesDto,
    @Json(name = "date") val dateUnixTime: Long,
    @Json(name = "owner_id") val ownerId: Long
)

data class AttachmentDto(
    @Json(name = "type") val type: String,
    @Json(name = "photo") val photo: PhotoDto? = null,
    @Json(name = "video") val video: VideoDto? = null,
    @Json(name = "audio") val audio: AudioDto? = null
)

data class AudioDto(
    @Json(name = "artist") val artist: String = "",
    @Json(name = "id") val id: Long = 0,
    @Json(name = "owner_id") val ownerId: Long = 0,
    @Json(name = "title") val title: String = "",
    @Json(name = "duration") val duration: Int = 0,
    @Json(name = "url") val url: String = "",
)

data class PhotoDto(
    @Json(name = "album_id") val albumId: Int = 0,
    @Json(name = "id") val id: Long = 0,
    @Json(name = "owner_id") val ownerId: Long = 0,
    @Json(name = "sizes") val sizes: List<SizeDto>? = null
)

data class VideoDto(
    @Json(name = "duration") val duration: Int = 0,
    @Json(name = "first_frame") val firstFrame: List<PhotoDto>? = null,
    @Json(name = "id") val id: Long = 0,
    @Json(name = "owner_id") val ownerId: Long = 0,
    @Json(name = "title") val title: String = "",
    @Json(name = "access_key") val accessKey: String = ""
)

data class VideoExtendedResponse(
    @Json(name = "response") val response: VideoExtendedDtoResponse
)

data class VideoExtendedDtoResponse(
    @Json(name = "count") val count: Int,
    @Json(name = "items") val items: List<VideoExtendedDataDto>
)

data class VideoExtendedDataDto(
    @Json(name = "duration") val duration: Int = 0,
    @Json(name = "first_frame") val firstFrame: List<PhotoDto>? = null,
    @Json(name = "id") val id: Long = 0,
    @Json(name = "owner_id") val ownerId: Long = 0,
    @Json(name = "title") val title: String = "",
    @Json(name = "player") val playerUrl: String = ""
)

data class LikesDto(
    @Json(name = "count") val count: Int = 0,
    @Json(name = "user_likes") val userLikes: Int = 0
)

data class SizeDto(
    @Json(name = "type") val type: String = "",
    @Json(name = "height") val height: Int = 0,
    @Json(name = "width") val width: Int = 0,
    @Json(name = "url") val url: String = ""
)
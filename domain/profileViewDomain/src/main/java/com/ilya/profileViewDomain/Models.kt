package com.ilya.profileViewDomain

import android.os.Parcelable
import com.ilya.core.appCommon.enums.FriendStatus
import com.ilya.core.appCommon.enums.PhotoSize
import com.ilya.core.appCommon.enums.Relation
import com.ilya.core.appCommon.enums.Sex
import kotlinx.parcelize.Parcelize

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
    val counters: Counters?,
    val photos: List<Photo>
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

@Parcelize
data class Post(
    val id: Long,
    val attachments: List<Attachment> = emptyList(),
    val likes: Likes,
    val date: String,
    val postOwner: PostOwner
) : Parcelable

@Parcelize
data class PostOwner(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val photoUrl: String
) : Parcelable

@Parcelize
data class Attachment(
    val type: String,
    val photo: Photo? = null,
    val video: VideoExtended? = null,
    val audio: Audio? = null
) : Parcelable

@Parcelize
data class Audio(
    val artist: String,
    val id: Long,
    val ownerId: Long,
    val title: String,
    val duration: Int,
    val url: String,
) : Parcelable

@Parcelize
data class Photo(
    val albumId: Int,
    val id: Long,
    val ownerId: Long,
    val sizes: List<Size>,
    val likes: Likes
) : Parcelable

@Parcelize
data class VideoExtended(
    val duration: Int = 0,
    val firstFrame: List<Photo>?,
    val id: Long,
    val ownerId: Long,
    val title: String,
    val playerUrl: String
) : Parcelable

@Parcelize
data class Likes(
    val count: Int,
    val userLikes: Boolean
) : Parcelable

@Parcelize
data class Size(
    val type: PhotoSize,
    val height: Int,
    val width: Int,
    val url: String
) : Parcelable
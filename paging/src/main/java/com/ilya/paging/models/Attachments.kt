package com.ilya.paging.models

import com.ilya.core.appCommon.enums.ObjectType


data class Audio(
    val artist: String,
    val id: Long,
    val ownerId: Long,
    val title: String,
    val duration: Int,
    val url: String,
)

data class Photo(
    val albumId: Int,
    val id: Long,
    val ownerId: Long,
    val likes: Likes?,
    val objectType: ObjectType = ObjectType.PHOTO,
    val sizes: List<Size>,
    val accessKey: String,
    val likeableCommonInfo: LikeableCommonInfo = LikeableCommonInfo(id, ownerId, likes, objectType)
)

data class Video(
    val duration: Int = 0,
    val firstFrame: List<FirstFrame>,
    val id: Long,
    val ownerId: Long,
    val title: String,
    val accessKey: String
)

data class VideoExtended(
    val duration: Int = 0,
    val firstFrame: List<FirstFrame>,
    val id: Long,
    val ownerId: Long,
    val likes: Likes?,
    val objectType: ObjectType = ObjectType.VIDEO,
    val title: String,
    val playerUrl: String,
    val likeableCommonInfo: LikeableCommonInfo = LikeableCommonInfo(id, ownerId, likes, objectType)
)
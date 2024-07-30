package com.ilya.paging.models

import com.ilya.core.appCommon.enums.ObjectType


abstract class Attachment

data class Audio(
    val artist: String,
    val id: Long,
    val ownerId: Long,
    val title: String,
    val duration: Int,
    val url: String,
) : Attachment()

data class Photo(
    val albumId: Int,
    override val id: Long,
    override val ownerId: Long,
    override val likes: Likes?,
    override val objectType: ObjectType = ObjectType.PHOTO,
    val sizes: List<Size>,
    val accessKey: String
) : Attachment(), Likeable

data class Video(
    val duration: Int = 0,
    val firstFrame: List<FirstFrame>,
    val id: Long,
    val ownerId: Long,
    val title: String,
    val accessKey: String
) : Attachment()

data class VideoExtended(
    val duration: Int = 0,
    val firstFrame: List<FirstFrame>,
    override val id: Long,
    override val ownerId: Long,
    override val likes: Likes?,
    override val objectType: ObjectType = ObjectType.VIDEO,
    val title: String,
    val playerUrl: String
) : Likeable
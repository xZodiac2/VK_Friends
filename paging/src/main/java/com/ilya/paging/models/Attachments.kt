package com.ilya.paging.models


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
    val title: String,
    val playerUrl: String
) : Likeable
package com.ilya.paging.models

import com.ilya.core.appCommon.enums.ObjectType
import com.ilya.core.appCommon.enums.PhotoSize

data class LikeableCommonInfo(
  val id: Long,
  val ownerId: Long,
  val likes: Likes?,
  val objectType: ObjectType
)

data class Likes(
  val count: Int,
  val userLikes: Boolean
)

data class Size(
  val type: PhotoSize,
  val height: Int,
  val width: Int,
  val url: String
)

data class FirstFrame(
  val url: String,
  val width: Int,
  val height: Int
)

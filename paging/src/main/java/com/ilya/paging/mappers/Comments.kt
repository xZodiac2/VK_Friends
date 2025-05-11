package com.ilya.paging.mappers

import com.ilya.core.appCommon.parseUnixTime
import com.ilya.data.retrofit.api.dto.CommentDto
import com.ilya.data.retrofit.api.dto.DEFAULT_REPLY_TO_ID
import com.ilya.paging.models.Comment
import com.ilya.paging.models.User

fun CommentDto.toComment(owner: User?, replyToUser: User?, thread: List<Comment>): Comment {

  val text = if (this.replyToUser == DEFAULT_REPLY_TO_ID) {
    text
  } else {
    text.substringAfter("|")
      .replace("]", "")
  }

  return Comment(
    date = parseUnixTime(date),
    fromId = fromId,
    postId = postId,
    ownerId = ownerId,
    id = id,
    text = text,
    thread = thread,
    likes = likes?.toLikes(),
    owner = owner,
    isDeleted = deleted,
    replyToComment = replyToComment,
    replyToUser = replyToUser
  )
}


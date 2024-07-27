package com.ilya.paging.mappers

import com.ilya.core.appCommon.parseUnixTime
import com.ilya.data.retrofit.api.dto.CommentDto
import com.ilya.data.retrofit.api.dto.ThreadDto
import com.ilya.paging.models.Comment
import com.ilya.paging.models.ThreadComment
import com.ilya.paging.models.User

fun CommentDto.toComment(owner: User?, thread: List<ThreadComment>): Comment {
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
        deleted = deleted
    )
}

fun ThreadDto.toThreadComment(replyToUser: User?, owner: User?): ThreadComment {
    val text = if (replyToUser == null) {
        text
    } else {
        /*
         * Before: [id657782631|Даня], привет
         * After:  Даня, привет
         */
        text
            .substringAfter("|")
            .replace("]", "")
    }

    return ThreadComment(
        date = parseUnixTime(date),
        fromId = fromId,
        replyToComment = replyToComment,
        postId = postId,
        replyToUser = replyToUser,
        owner = owner,
        id = id,
        text = text,
        likes = likes.toLikes()
    )
}

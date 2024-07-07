package com.ilya.core.appCommon.enums

import com.ilya.core.appCommon.enums.FriendStatus.FRIENDS
import com.ilya.core.appCommon.enums.FriendStatus.NOT_FRIENDS
import com.ilya.core.appCommon.enums.FriendStatus.SUBSCRIBED
import com.ilya.core.appCommon.enums.FriendStatus.WAITING

enum class FriendStatus(val value: Int) {
    NOT_FRIENDS(0),
    WAITING(1),
    SUBSCRIBED(2),
    FRIENDS(3);
}

fun FriendStatus.toggled(): FriendStatus {
    return when (this) {
        FRIENDS -> SUBSCRIBED
        WAITING -> NOT_FRIENDS
        NOT_FRIENDS -> WAITING
        SUBSCRIBED -> FRIENDS
    }
}

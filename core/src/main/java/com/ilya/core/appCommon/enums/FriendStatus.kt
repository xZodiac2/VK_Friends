package com.ilya.core.appCommon.enums

import com.ilya.core.appCommon.base.Toggleable


enum class FriendStatus(val value: Int) : Toggleable<FriendStatus> {
    NOT_FRIENDS(0),
    WAITING(1),
    SUBSCRIBED(2),
    FRIENDS(3);

    override fun toggled(): FriendStatus {
        return when (this) {
            FRIENDS -> SUBSCRIBED
            WAITING -> NOT_FRIENDS
            NOT_FRIENDS -> WAITING
            SUBSCRIBED -> FRIENDS
        }
    }

}
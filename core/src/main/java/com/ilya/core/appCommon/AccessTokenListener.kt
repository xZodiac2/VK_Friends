package com.ilya.core.appCommon

import com.vk.id.AccessToken

fun interface AccessTokenListener {
    fun onChange(accessToken: AccessToken?)
}
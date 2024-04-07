package com.ilya.core.appCommon

import com.vk.id.AccessToken

fun interface AccessTokenOperationsListener {
    fun onOperation(accessToken: AccessToken?)
}
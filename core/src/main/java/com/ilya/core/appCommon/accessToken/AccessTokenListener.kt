package com.ilya.core.appCommon.accessToken

import com.vk.id.AccessToken

fun interface AccessTokenListener {
  fun onChange(accessToken: AccessToken?)
}
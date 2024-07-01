package com.ilya.vkfriends.app

import android.app.Application
import com.ilya.core.appCommon.AccessTokenManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class VkFriendsApplication : Application()
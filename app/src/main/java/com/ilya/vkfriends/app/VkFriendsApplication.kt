package com.ilya.vkfriends.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VkFriendsApplication : Application() {
    companion object {
        const val IS_DEVELOPING = true
    }
}
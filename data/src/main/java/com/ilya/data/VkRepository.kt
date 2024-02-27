package com.ilya.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class VkRepository @Inject internal constructor(private val vkApi: VkApi) {
    
    suspend fun getFriends(accessToken: String, fields: List<String> = listOf("photo_200_orig")): List<User> {
        return withContext(Dispatchers.IO) { vkApi.getFriends(accessToken, fields) }.response.items
    }
    
}
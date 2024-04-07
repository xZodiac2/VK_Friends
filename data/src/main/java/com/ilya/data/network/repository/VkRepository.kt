package com.ilya.data.network.repository

import com.ilya.data.network.RemoteRepository
import com.ilya.data.network.retrofit.UserDto
import com.ilya.data.network.retrofit.VkApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


internal class VkRepository @Inject constructor(private val vkApi: VkApi) : RemoteRepository {

    override suspend fun getFriends(
        accessToken: String,
        count: Int,
        offset: Int,
        fields: List<String>
    ): List<UserDto> {
        return withContext(Dispatchers.IO) {
            vkApi.getFriends(
                accessToken = accessToken,
                fields = fields,
                count = count,
                offset = offset
            )
        }.responseData.items
    }

    override suspend fun searchUsers(
        accessToken: String,
        count: Int,
        offset: Int,
        query: String,
        fields: List<String>
    ): List<UserDto> {
        return withContext(Dispatchers.IO) {
            vkApi.searchUsers(
                accessToken = accessToken,
                query = query,
                fields = fields,
                count = count,
                offset = offset
            )
        }.responseData.items
    }

    override suspend fun getSuggestions(
        accessToken: String,
        count: Int,
        offset: Int,
        fields: List<String>
    ): List<UserDto> {
        return withContext(Dispatchers.IO) {
            vkApi.getSuggestions(
                accessToken = accessToken,
                fields = fields,
                count = count,
                offset = offset
            )
        }.responseData.items
    }
}
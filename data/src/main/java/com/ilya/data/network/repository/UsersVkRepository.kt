package com.ilya.data.network.repository

import com.ilya.data.network.UsersRemoteRepository
import com.ilya.data.network.retrofit.api.UserDto
import com.ilya.data.network.retrofit.api.UsersVkApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


internal class UsersVkRepository @Inject constructor(
    private val usersVkApi: UsersVkApi
) : UsersRemoteRepository {

    override suspend fun getFriends(
        accessToken: String,
        count: Int,
        offset: Int,
        fields: List<String>
    ): List<UserDto> {
        return withContext(Dispatchers.IO) {
            usersVkApi.getFriends(
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
            usersVkApi.searchUsers(
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
            usersVkApi.getSuggestions(
                accessToken = accessToken,
                fields = fields,
                count = count,
                offset = offset
            )
        }.responseData.items
    }

}
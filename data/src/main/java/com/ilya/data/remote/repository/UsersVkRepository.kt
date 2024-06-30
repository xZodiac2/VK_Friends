package com.ilya.data.remote.repository

import com.ilya.data.remote.UsersRemoteRepository
import com.ilya.data.remote.retrofit.api.UsersVkApi
import com.ilya.data.remote.retrofit.api.dto.UserDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Inject


internal class UsersVkRepository @Inject constructor(
    retrofit: Retrofit
) : UsersRemoteRepository {

    private val usersVkApi = retrofit.create<UsersVkApi>()

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
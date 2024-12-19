package com.ilya.data

import com.ilya.data.retrofit.api.dto.UserDto

interface UsersRemoteRepository {

    suspend fun getFriends(
        accessToken: String,
        count: Int,
        offset: Int,
        fields: List<String>
    ): List<UserDto>

    suspend fun searchUsers(
        accessToken: String,
        count: Int,
        offset: Int,
        query: String,
        fields: List<String>
    ): List<UserDto>

    suspend fun getSuggestions(
        accessToken: String,
        count: Int,
        offset: Int,
        fields: List<String>
    ): List<UserDto>

}
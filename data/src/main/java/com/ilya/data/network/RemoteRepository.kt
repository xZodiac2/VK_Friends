package com.ilya.data.network

import com.ilya.data.network.retrofit.UserDto

interface RemoteRepository {

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
package com.ilya.data.network.repository

import com.ilya.core.appCommon.enums.NameCase
import com.ilya.data.network.UserDataRemoteRepository
import com.ilya.data.network.retrofit.api.UserDataVkApi
import com.ilya.data.network.retrofit.api.UserDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class UserDataVkRepository @Inject constructor(
    private val userDataVkApi: UserDataVkApi
) : UserDataRemoteRepository {

    override suspend fun getUser(
        accessToken: String,
        userId: Long,
        nameCase: NameCase,
        fields: List<String>
    ): UserDto {
        return withContext(Dispatchers.IO) {
            userDataVkApi.getUserData(
                accessToken = accessToken,
                userId = userId,
                fields = fields.joinToString(","),
                nameCase = nameCase.value
            )
        }.response.first()
    }

}
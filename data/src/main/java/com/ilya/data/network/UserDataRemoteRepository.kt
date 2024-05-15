package com.ilya.data.network

import com.ilya.core.appCommon.enums.NameCase
import com.ilya.data.network.retrofit.api.UserDto

interface UserDataRemoteRepository {

    suspend fun getUser(
        accessToken: String,
        userId: Long,
        nameCase: NameCase = NameCase.NOMINATIVE,
        fields: List<String>
    ): UserDto

}
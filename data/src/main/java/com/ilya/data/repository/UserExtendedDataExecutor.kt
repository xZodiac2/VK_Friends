package com.ilya.data.repository

import com.ilya.data.VkApiExecutor
import com.ilya.data.retrofit.api.UserDataExecuteVkApi
import com.ilya.data.retrofit.api.dto.UserExtendedResponseData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Inject

internal class UserExtendedDataExecutor @Inject constructor(
    retrofit: Retrofit
) : VkApiExecutor<UserExtendedResponseData> {

    private val api = retrofit.create<UserDataExecuteVkApi>()

    override suspend fun execute(accessToken: String, code: String): UserExtendedResponseData {
        return withContext(Dispatchers.IO) { api.execute(accessToken, code) }.response
    }

}
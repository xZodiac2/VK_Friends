package com.ilya.data.remote.repository.executor

import com.ilya.data.remote.VkApiExecutor
import com.ilya.data.remote.retrofit.api.UserDataExecuteVkApi
import com.ilya.data.remote.retrofit.api.dto.UserExtendedResponseData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Inject

internal class UserExtendedDataExecutor @Inject constructor(
    retrofit: Retrofit
) : VkApiExecutor<UserExtendedResponseData> {

    private val executeVkApi = retrofit.create<UserDataExecuteVkApi>()

    override suspend fun execute(accessToken: String, code: String): UserExtendedResponseData {
        return withContext(Dispatchers.IO) { executeVkApi.execute(accessToken, code) }.response
    }

}
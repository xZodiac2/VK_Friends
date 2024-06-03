package com.ilya.data.network.repository.executor

import com.ilya.data.network.VkApiExecutor
import com.ilya.data.network.retrofit.api.UserDataExecuteVkApi
import com.ilya.data.network.retrofit.api.UserExtendedResponseData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class UserExtendedDataExecutor @Inject constructor(
    private val executeVkApi: UserDataExecuteVkApi
) : VkApiExecutor<UserExtendedResponseData> {

    override suspend fun execute(accessToken: String, code: String): UserExtendedResponseData {
        return withContext(Dispatchers.IO) { executeVkApi.execute(accessToken, code) }.response
    }

}
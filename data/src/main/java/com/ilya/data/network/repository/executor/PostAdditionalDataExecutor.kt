package com.ilya.data.network.repository.executor

import com.ilya.data.network.VkApiExecutor
import com.ilya.data.network.retrofit.api.AdditionalPostData
import com.ilya.data.network.retrofit.api.PostsDataExecuteVkApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class PostAdditionalDataExecutor @Inject constructor(
    private val executeVkApi: PostsDataExecuteVkApi
) : VkApiExecutor<AdditionalPostData> {

    override suspend fun execute(accessToken: String, code: String): AdditionalPostData {
        return withContext(Dispatchers.IO) { executeVkApi.execute(accessToken, code) }.response
    }

}
package com.ilya.data.remote.repository.executor

import com.ilya.data.remote.VkApiExecutor
import com.ilya.data.remote.retrofit.api.PostsDataExecuteVkApi
import com.ilya.data.remote.retrofit.api.dto.AdditionalPostData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Inject

internal class PostAdditionalDataExecutor @Inject constructor(
    retrofit: Retrofit
) : VkApiExecutor<AdditionalPostData> {

    private val executeVkApi = retrofit.create<PostsDataExecuteVkApi>()

    override suspend fun execute(accessToken: String, code: String): AdditionalPostData {
        return withContext(Dispatchers.IO) { executeVkApi.execute(accessToken, code) }.response
    }

}
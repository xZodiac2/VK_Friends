package com.ilya.data.remote

interface VkApiExecutor<T> {

    suspend fun execute(
        accessToken: String,
        code: String
    ): T

}
package com.ilya.data.network

interface VkApiExecutor<T> {

    suspend fun execute(
        accessToken: String,
        code: String
    ): T

}
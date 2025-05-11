package com.ilya.data

interface VkApiExecutor<T> {

  suspend fun execute(
    accessToken: String,
    code: String
  ): T

}
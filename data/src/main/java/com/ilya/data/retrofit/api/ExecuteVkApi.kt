package com.ilya.data.retrofit.api

import com.ilya.data.retrofit.CURRENT_API_VERSION
import com.ilya.data.retrofit.api.dto.UserExtendedResponse
import retrofit2.http.POST
import retrofit2.http.Query



internal interface UserDataExecuteVkApi {

    @POST("execute?v=$CURRENT_API_VERSION")
    suspend fun execute(
        @Query("access_token") accessToken: String,
        @Query("code") code: String
    ): UserExtendedResponse

}


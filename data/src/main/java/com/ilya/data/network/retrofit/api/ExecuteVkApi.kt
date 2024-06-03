package com.ilya.data.network.retrofit.api

import com.ilya.data.network.retrofit.CURRENT_API_VERSION
import com.squareup.moshi.Json
import retrofit2.http.POST
import retrofit2.http.Query

internal interface PostsDataExecuteVkApi {

    @POST("execute?v=$CURRENT_API_VERSION")
    suspend fun execute(
        @Query("access_token") accessToken: String,
        @Query("code") code: String
    ): AdditionalPostDataResponse

}

data class AdditionalPostDataResponse(
    @Json(name = "response") val response: AdditionalPostData
)

data class AdditionalPostData(
    @Json(name = "videos") val videos: AdditionalVideosDataDto? = null,
    @Json(name = "post_owner") val postOwner: AdditionalPostOwnerDto,
    @Json(name = "photos") val photos: AdditionalPhotosDataDto? = null
)

data class AdditionalPhotosDataDto(
    @Json(name = "post_id") val postId: Long,
    @Json(name = "items") val items: List<PhotoDto>
)

data class AdditionalVideosDataDto(
    @Json(name = "post_id") val postId: Long,
    @Json(name = "items") val items: List<VideoExtendedDataDto>
)

data class AdditionalPostOwnerDto(
    @Json(name = "post_id") val postId: Long,
    @Json(name = "data") val data: UserDto
)

internal interface UserDataExecuteVkApi {

    @POST("execute?v=$CURRENT_API_VERSION")
    suspend fun execute(
        @Query("access_token") accessToken: String,
        @Query("code") code: String
    ): UserExtendedResponse

}

data class UserExtendedResponse(
    @Json(name = "response") val response: UserExtendedResponseData
)

data class UserExtendedResponseData(
    @Json(name = "user") val user: UserDto,
    @Json(name = "partner") val partner: UserDto? = null,
    @Json(name = "photos") val photos: List<PhotoDto>
)
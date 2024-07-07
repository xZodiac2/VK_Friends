package com.ilya.data.remote.repository

import com.ilya.data.remote.GroupsRemoteRepository
import com.ilya.data.remote.retrofit.api.GroupsVkApi
import com.ilya.data.remote.retrofit.api.dto.GroupDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import javax.inject.Inject

internal class GroupVkRepository @Inject constructor(
    retrofit: Retrofit
) : GroupsRemoteRepository {

    private val api = retrofit.create(GroupsVkApi::class.java)

    override suspend fun getGroup(accessToken: String, groupId: Long): GroupDto {
        return withContext(Dispatchers.IO) {
            api.getGroup(accessToken, groupId)
        }.response.groups.first()
    }

}
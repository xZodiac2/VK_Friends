package com.ilya.data.repository

import com.ilya.data.GroupsRemoteRepository
import com.ilya.data.retrofit.api.GroupsVkApi
import com.ilya.data.retrofit.api.dto.GroupDto
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
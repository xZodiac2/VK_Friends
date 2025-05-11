package com.ilya.data

import com.ilya.data.retrofit.api.dto.GroupDto

interface GroupsRemoteRepository {
  suspend fun getGroup(accessToken: String, groupId: Long): GroupDto
}
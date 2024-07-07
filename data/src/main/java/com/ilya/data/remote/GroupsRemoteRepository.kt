package com.ilya.data.remote

import com.ilya.data.remote.retrofit.api.dto.GroupDto

interface GroupsRemoteRepository {

    suspend fun getGroup(accessToken: String, groupId: Long): GroupDto

}
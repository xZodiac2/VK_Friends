package com.ilya.data.remote.di

import com.ilya.data.remote.FriendsManageRemoteRepository
import com.ilya.data.remote.GroupsRemoteRepository
import com.ilya.data.remote.LikesRemoteRepository
import com.ilya.data.remote.UserDataRemoteRepository
import com.ilya.data.remote.UsersRemoteRepository
import com.ilya.data.remote.VkApiExecutor
import com.ilya.data.remote.repository.FriendsManageVkRepository
import com.ilya.data.remote.repository.GroupVkRepository
import com.ilya.data.remote.repository.LikesVkRepository
import com.ilya.data.remote.repository.UserDataVkRepository
import com.ilya.data.remote.repository.UserExtendedDataExecutor
import com.ilya.data.remote.repository.UsersVkRepository
import com.ilya.data.remote.retrofit.api.dto.UserExtendedResponseData
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
internal interface RemoteRepositoryModule {

    @Binds
    fun bindUsersRemoteRepository(impl: UsersVkRepository): UsersRemoteRepository

    @Binds
    fun bindUserDataRemoteRepository(impl: UserDataVkRepository): UserDataRemoteRepository

    @Binds
    fun bindFriendsManageRemoteRepository(impl: FriendsManageVkRepository): FriendsManageRemoteRepository

    @Binds
    fun bindExtendedUserDataExecutor(impl: UserExtendedDataExecutor): VkApiExecutor<UserExtendedResponseData>

    @Binds
    fun bindLikesRemoteRepository(impl: LikesVkRepository): LikesRemoteRepository

    @Binds
    fun bindGroupsRemoteRepository(impl: GroupVkRepository): GroupsRemoteRepository

}
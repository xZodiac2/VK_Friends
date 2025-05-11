package com.ilya.data.di

import com.ilya.data.FriendsManageRemoteRepository
import com.ilya.data.GroupsRemoteRepository
import com.ilya.data.LikesRemoteRepository
import com.ilya.data.UserDataRemoteRepository
import com.ilya.data.UsersRemoteRepository
import com.ilya.data.VkApiExecutor
import com.ilya.data.WallRemoteRepository
import com.ilya.data.repository.FriendsManageVkRepository
import com.ilya.data.repository.GroupVkRepository
import com.ilya.data.repository.LikesVkRepository
import com.ilya.data.repository.UserDataVkRepository
import com.ilya.data.repository.UserExtendedDataExecutor
import com.ilya.data.repository.UsersVkRepository
import com.ilya.data.repository.WallVkRepository
import com.ilya.data.retrofit.api.dto.UserExtendedResponseData
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

  @Binds
  fun bindWallRemoteRepository(impl: WallVkRepository): WallRemoteRepository

}
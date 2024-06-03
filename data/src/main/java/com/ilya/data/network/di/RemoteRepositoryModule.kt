package com.ilya.data.network.di

import com.ilya.data.network.FriendsManageRemoteRepository
import com.ilya.data.network.UserDataRemoteRepository
import com.ilya.data.network.UsersRemoteRepository
import com.ilya.data.network.VkApiExecutor
import com.ilya.data.network.repository.FriendsManageVkRepository
import com.ilya.data.network.repository.UserDataVkRepository
import com.ilya.data.network.repository.UsersVkRepository
import com.ilya.data.network.repository.executor.PostAdditionalDataExecutor
import com.ilya.data.network.repository.executor.UserExtendedDataExecutor
import com.ilya.data.network.retrofit.api.AdditionalPostData
import com.ilya.data.network.retrofit.api.UserExtendedResponseData
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
    fun bindPostsAdditionalDataExecutor(impl: PostAdditionalDataExecutor): VkApiExecutor<AdditionalPostData>

}
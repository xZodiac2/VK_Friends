package com.ilya.data.network.di

import com.ilya.data.network.FriendsManageRemoteRepository
import com.ilya.data.network.UserDataRemoteRepository
import com.ilya.data.network.UsersRemoteRepository
import com.ilya.data.network.repository.FriendsManageVkRepository
import com.ilya.data.network.repository.UserDataVkRepository
import com.ilya.data.network.repository.UsersVkRepository
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

}
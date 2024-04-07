package com.ilya.data.local

import com.ilya.data.local.database.FriendEntity
import com.ilya.data.local.database.UserEntity
import com.ilya.data.local.repository.FriendsLocalRepository
import com.ilya.data.local.repository.UsersLocalRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
internal interface LocalRepositoryModule {

    @Binds
    fun bindUsersRepo(impl: UsersLocalRepository): LocalRepository<UserEntity>

    @Binds
    fun bindFriendsRepo(impl: FriendsLocalRepository): LocalRepository<FriendEntity>

}
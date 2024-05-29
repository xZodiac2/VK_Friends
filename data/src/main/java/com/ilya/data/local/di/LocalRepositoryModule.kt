package com.ilya.data.local.di

import com.ilya.data.local.LocalRepository
import com.ilya.data.local.database.FriendEntity
import com.ilya.data.local.database.PostEntity
import com.ilya.data.local.database.UserEntity
import com.ilya.data.local.repository.FriendsLocalRepository
import com.ilya.data.local.repository.PostsLocalRepository
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

    @Binds
    fun bindPostsRepo(impl: PostsLocalRepository): LocalRepository<PostEntity>

}
package com.ilya.data.local.di

import com.ilya.data.local.LocalRepository
import com.ilya.data.local.database.entities.FriendPagingEntity
import com.ilya.data.local.database.entities.PostWithAttachmentsAndOwner
import com.ilya.data.local.database.entities.UserPagingEntity
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
    fun bindUsersRepo(impl: UsersLocalRepository): LocalRepository<UserPagingEntity>

    @Binds
    fun bindFriendsRepo(impl: FriendsLocalRepository): LocalRepository<FriendPagingEntity>

    @Binds
    fun bindPostsRepo(impl: PostsLocalRepository): LocalRepository<PostWithAttachmentsAndOwner>

}
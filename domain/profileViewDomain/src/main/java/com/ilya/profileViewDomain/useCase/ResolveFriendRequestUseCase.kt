package com.ilya.profileViewDomain.useCase

import com.ilya.core.appCommon.UseCase
import com.ilya.core.appCommon.enums.FriendStatus
import com.ilya.core.appCommon.enums.toggled
import com.ilya.data.remote.FriendsManageRemoteRepository
import com.ilya.profileViewDomain.models.User
import javax.inject.Inject

class ResolveFriendRequestUseCase @Inject constructor(
    private val friendsManageRepo: FriendsManageRemoteRepository
) : UseCase<ResolveFriendRequestUseCase.InvokeData, FriendStatus> {

    override suspend fun invoke(data: InvokeData): FriendStatus = with(data) {
        when (user.friendStatus) {
            FriendStatus.FRIENDS -> friendsManageRepo.deleteFriend(accessToken, user.id)
            FriendStatus.WAITING -> friendsManageRepo.deleteFriend(accessToken, user.id)
            FriendStatus.NOT_FRIENDS -> friendsManageRepo.addFriend(accessToken, user.id)
            FriendStatus.SUBSCRIBED -> friendsManageRepo.addFriend(accessToken, user.id)

        }

        return data.user.friendStatus.toggled()
    }

    data class InvokeData(
        val accessToken: String,
        val user: User
    )

}


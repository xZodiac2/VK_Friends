package com.ilya.profileViewDomain.useCase

import com.ilya.core.appCommon.base.UseCase
import com.ilya.core.appCommon.enums.FriendStatus
import com.ilya.data.FriendsManageRemoteRepository
import com.ilya.profileViewDomain.User
import javax.inject.Inject

class ResolveFriendRequestUseCase @Inject constructor(
  private val friendsManageRepo: FriendsManageRemoteRepository
) : UseCase<ResolveFriendRequestUseCase.InvokeData, Unit> {

  override suspend fun invoke(data: InvokeData) = with(data) {
    when (user.friendStatus) {
      FriendStatus.FRIENDS -> friendsManageRepo.deleteFriend(accessToken, user.id)
      FriendStatus.WAITING -> friendsManageRepo.deleteFriend(accessToken, user.id)
      FriendStatus.NOT_FRIENDS -> friendsManageRepo.addFriend(accessToken, user.id)
      FriendStatus.SUBSCRIBED -> friendsManageRepo.addFriend(accessToken, user.id)
    }
  }

  data class InvokeData(
    val accessToken: String,
    val user: User
  )

}


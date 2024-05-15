package com.ilya.profileViewDomain.useCase

import com.ilya.core.appCommon.UseCase
import com.ilya.core.appCommon.enums.NameCase
import com.ilya.core.appCommon.enums.Sex
import com.ilya.data.network.UserDataRemoteRepository
import com.ilya.profileViewDomain.User
import com.ilya.profileViewDomain.toUser
import javax.inject.Inject

class GetUserDataUseCase @Inject constructor(
    private val userDataRepo: UserDataRemoteRepository
) : UseCase<GetUserUseCaseData, User> {

    override suspend fun invoke(data: GetUserUseCaseData): User {
        val userData = userDataRepo.getUser(
            accessToken = data.accessToken,
            userId = data.userId,
            fields = FIELDS
        ).toUser()
        val partner = userData.partner ?: return userData

        val partnerExtended = userDataRepo.getUser(
            accessToken = data.accessToken,
            userId = partner.id,
            nameCase = if (userData.sex == Sex.WOMAN) NameCase.CREATIVE else NameCase.PREPOSITIONAL,
            fields = FIELDS
        ).toUser()

        return userData.copy(partnerExtended = partnerExtended)
    }

    companion object {
        private val FIELDS = listOf(
            "photo_200_orig",
            "city",
            "bdate",
            "status",
            "friend_status",
            "relation",
            "sex",
            "counters"
        )
    }

}

data class GetUserUseCaseData(
    val accessToken: String,
    val userId: Long,
)

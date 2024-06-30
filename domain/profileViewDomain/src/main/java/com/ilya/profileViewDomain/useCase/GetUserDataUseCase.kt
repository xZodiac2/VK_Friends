package com.ilya.profileViewDomain.useCase

import com.ilya.core.appCommon.UseCase
import com.ilya.core.appCommon.enums.NameCase
import com.ilya.data.remote.VkApiExecutor
import com.ilya.data.remote.retrofit.api.dto.UserExtendedResponseData
import com.ilya.profileViewDomain.mappers.toUser
import com.ilya.profileViewDomain.models.User
import javax.inject.Inject

class GetUserDataUseCase @Inject constructor(
    private val vkApiExecutor: VkApiExecutor<UserExtendedResponseData>
) : UseCase<GetUserUseCaseData, User> {

    override suspend fun invoke(data: GetUserUseCaseData): User {
        val vkApiRequest = """
            var user = API.users.get({
                "user_ids": [${data.userId}],
                "fields": [${FIELDS.joinToString(",") { "\"$it\"" }}]
            })[0];
            var partner = null;
            if (user.sex == 1) {
                partner = API.users.get({
                    "user_ids": [user.relation_partner.id],
                    "name_case": "${NameCase.CREATIVE.value}"
                })[0];    
            } else {
                partner =  API.users.get({
                    "user_ids": [user.relation_partner.id],
                    "name_case": "${NameCase.PREPOSITIONAL.value}"
                })[0];
            }
            var photos = API.photos.getAll({
                "owner_id": user.id,
                "count": 6,
                "extended": 1
            });

            return {
                "user": user,
                "partner": partner,
                "photos": photos.items
            };
        """.trimIndent()

        val response = vkApiExecutor.execute(
            accessToken = data.accessToken,
            code = vkApiRequest
        )

        val photos = response.photos
        val user = response.user.toUser(photos)
        val partner = response.partner?.toUser(emptyList()) ?: return user

        return user.copy(partnerExtended = partner)
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

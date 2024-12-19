package com.ilya.profileViewDomain.useCase

import com.ilya.core.appCommon.base.UseCase
import com.ilya.core.appCommon.enums.NameCase
import com.ilya.data.VkApiExecutor
import com.ilya.data.retrofit.api.dto.UserExtendedResponseData
import com.ilya.profileViewDomain.User
import com.ilya.profileViewDomain.toPartner
import com.ilya.profileViewDomain.toUser
import javax.inject.Inject

class GetUserDataUseCase @Inject constructor(
    private val vkApiExecutor: VkApiExecutor<UserExtendedResponseData>
) : UseCase<GetUserDataUseCase.InvokeData, User> {

    override suspend fun invoke(data: InvokeData): User {
        val vkScriptRequest = """
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
            code = vkScriptRequest
        )

        val photos = response.photos
        val user = response.user.toUser(photos ?: emptyList())
        val partner = response.partner?.toPartner() ?: return user

        return user.copy(partner = partner)
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

    data class InvokeData(
        val accessToken: String,
        val userId: Long,
    )

}


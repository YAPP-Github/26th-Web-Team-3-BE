package com.yapp.lettie.api.auth.client.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.yapp.lettie.api.auth.service.dto.AuthUserInfoDto

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class KakaoUserInfoResponse(
    val id: Long,
    val connectedAt: String?,
    val properties: Properties?,
    val kakaoAccount: KakaoAccount?,
) {
    data class Properties(
        val nickname: String?,
        val profileImage: String?,
        val thumbnailImage: String?,
    )

    data class KakaoAccount(
        val profileNicknameNeedsAgreement: Boolean?,
        val profileImageNeedsAgreement: Boolean?,
        val profile: Profile?,
    ) {
        data class Profile(
            val nickname: String?,
            val thumbnailImageUrl: String?,
            val profileImageUrl: String?,
            val isDefaultImage: Boolean?,
            val isDefaultNickname: Boolean?,
        )
    }

    fun toDto(): AuthUserInfoDto =
        AuthUserInfoDto(
            id = this.id.toString(),
            email = "",
            name = this.properties?.nickname,
        )
}

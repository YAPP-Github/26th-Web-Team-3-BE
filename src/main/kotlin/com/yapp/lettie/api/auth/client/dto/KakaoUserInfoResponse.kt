package com.yapp.lettie.api.auth.client.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.yapp.lettie.api.auth.service.dto.KakaoUserInfoDto

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

    fun toDto(): KakaoUserInfoDto =
        KakaoUserInfoDto(
            id = this.id,
            connectedAt = this.connectedAt,
            properties =
                KakaoUserInfoDto.Properties(
                    nickname = this.properties?.nickname,
                    profileImage = this.properties?.profileImage,
                    thumbnailImage = this.properties?.thumbnailImage,
                ),
            kakaoAccount =
                KakaoUserInfoDto.KakaoAccount(
                    profileNicknameNeedsAgreement = this.kakaoAccount?.profileNicknameNeedsAgreement,
                    profileImageNeedsAgreement = this.kakaoAccount?.profileImageNeedsAgreement,
                    profile =
                        KakaoUserInfoDto.KakaoAccount.Profile(
                            nickname = this.kakaoAccount?.profile?.nickname,
                            thumbnailImageUrl = this.kakaoAccount?.profile?.thumbnailImageUrl,
                            profileImageUrl = this.kakaoAccount?.profile?.profileImageUrl,
                            isDefaultImage = this.kakaoAccount?.profile?.isDefaultImage,
                            isDefaultNickname = this.kakaoAccount?.profile?.isDefaultNickname,
                        ),
                ),
        )
}

package com.yapp.demo.api.auth.service.dto

data class KakaoUserInfoDto(
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
}

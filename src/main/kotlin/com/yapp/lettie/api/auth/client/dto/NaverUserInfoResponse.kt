package com.yapp.lettie.api.auth.client.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.yapp.lettie.api.auth.service.dto.AuthUserInfoDto

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class NaverUserInfoResponse(
    val resultcode: String,
    val message: String,
    val response: Response,
) {
    data class Response(
        val id: String,
        val nickname: String?,
        val profileImage: String?,
        val age: String?,
        val gender: String?,
        val email: String,
        val mobile: String?,
        val mobileE164: String?,
        val name: String?,
        val birthday: String?,
        val birthyear: String?,
    )

    fun toDto(): AuthUserInfoDto =
        AuthUserInfoDto(
            id = this.response.id,
            email = this.response.email,
            name = this.response.name ?: this.response.nickname,
        )
}

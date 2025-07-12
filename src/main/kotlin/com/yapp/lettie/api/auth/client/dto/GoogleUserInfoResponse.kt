package com.yapp.lettie.api.auth.client.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.yapp.lettie.api.auth.service.dto.AuthUserInfoDto

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class GoogleUserInfoResponse(
    val id: String,
    val email: String,
    val verifiedEmail: Boolean?,
    val name: String?,
    val givenName: String?,
    val picture: String?,
) {
    fun toDto(): AuthUserInfoDto =
        AuthUserInfoDto(
            id = this.id,
            email = this.email,
            name = this.name ?: this.givenName,
        )
}

package com.yapp.demo.api.auth.client.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class KakaoTokenResponse(
    val accessToken: String,
    val tokenType: String,
    val refreshToken: String,
    val expiresIn: Int,
    val scope: String,
    val refreshTokenExpiresIn: Int,
) {
    fun getBearerToken(): String = "Bearer $accessToken"
}

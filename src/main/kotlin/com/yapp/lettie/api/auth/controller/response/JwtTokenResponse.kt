package com.yapp.lettie.api.auth.controller.response

import com.yapp.lettie.api.auth.service.dto.JwtTokenDto
import io.swagger.v3.oas.annotations.media.Schema

data class JwtTokenResponse(
    @Schema(description = "유저 닉네임", example = "얍")
    val nickname: String?,
    @Schema(description = "로그인 성공 여부", example = "true")
    val success: Boolean = true,
) {
    companion object {
        fun of(jwtTokenDto: JwtTokenDto): JwtTokenResponse =
            JwtTokenResponse(
                nickname = jwtTokenDto.nickname,
            )
    }
}

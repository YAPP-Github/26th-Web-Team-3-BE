package com.yapp.demo.api.auth.controller.response

import com.yapp.demo.api.auth.service.dto.JwtTokenDto
import io.swagger.v3.oas.annotations.media.Schema

data class JwtTokenResponse(
    @Schema(
        description = "jwt token",
        example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTYyMjEwNjQwMCwiZXhwIjoxNjIyMTA2NDAwfQ.3",
    )
    val token: String,
    @Schema(description = "유저 닉네임", example = "얍")
    val nickname: String?,
) {
    companion object {
        fun of(jwtTokenDto: JwtTokenDto): JwtTokenResponse =
            JwtTokenResponse(
                jwtTokenDto.token,
                nickname = jwtTokenDto.nickname,
            )
    }
}

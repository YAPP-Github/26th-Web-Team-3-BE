package com.yapp.demo.api.auth.service.dto

import com.yapp.demo.domain.user.entity.User

data class JwtTokenDto(
    val token: String,
    val nickname: String?,
) {
    companion object {
        fun of(
            token: String,
            user: User,
        ): JwtTokenDto = JwtTokenDto(token, user.nickname)
    }
}

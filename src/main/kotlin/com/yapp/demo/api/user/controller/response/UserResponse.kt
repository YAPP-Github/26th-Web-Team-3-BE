package com.yapp.demo.api.user.controller.response

import com.yapp.demo.api.user.service.dto.UserDto
import io.swagger.v3.oas.annotations.media.Schema

data class UserResponse(
    @Schema(description = "유저 식별 ID")
    val id: Long,
    @Schema(description = "유저 닉네임")
    val nickname: String? = null,
    @Schema(description = "유저 이메일")
    val email: String? = null,
) {
    companion object {
        fun of(userDto: UserDto): UserResponse = UserResponse(userDto.id, userDto.nickname, userDto.email)
    }
}

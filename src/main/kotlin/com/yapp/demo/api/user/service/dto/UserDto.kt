package com.yapp.demo.api.user.service.dto

import com.yapp.demo.domain.user.entity.User

data class UserDto(
    val id: Long,
    val nickname: String? = null,
    val email: String? = null,
) {
    companion object {
        fun of(user: User): UserDto = UserDto(user.id, user.nickname, user.email)
    }
}

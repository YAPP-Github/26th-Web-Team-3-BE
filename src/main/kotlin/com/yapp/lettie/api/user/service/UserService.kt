package com.yapp.lettie.api.user.service

import com.yapp.lettie.api.user.service.dto.UserCountDto
import com.yapp.lettie.api.user.service.dto.UserDto
import com.yapp.lettie.api.user.service.reader.UserReader
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userReader: UserReader,
) {
    fun getUserInfo(userId: Long): UserDto {
        val user = userReader.getById(userId)
        return UserDto.of(user)
    }

    fun getUserTotalCount(): UserCountDto = UserCountDto.from(userReader.getUserTotalCount())
}

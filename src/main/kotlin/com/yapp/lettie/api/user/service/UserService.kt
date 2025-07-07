package com.yapp.lettie.api.user.service

import com.yapp.lettie.api.user.service.dto.UserDto
import com.yapp.lettie.common.error.ErrorMessages.USER_NOT_FOUND
import com.yapp.lettie.common.exception.ApiErrorException
import com.yapp.lettie.domain.user.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
) {
    fun getUserInfo(userId: Long): UserDto {
        val user = userRepository.findByIdOrNull(userId) ?: throw ApiErrorException(USER_NOT_FOUND)
        return UserDto.of(user)
    }
}

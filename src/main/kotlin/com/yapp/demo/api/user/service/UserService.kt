package com.yapp.demo.api.user.service

import com.yapp.demo.api.user.service.dto.UserDto
import com.yapp.demo.common.error.ErrorMessages.USER_NOT_FOUND
import com.yapp.demo.common.exception.ApiErrorException
import com.yapp.demo.domain.user.repository.UserRepository
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

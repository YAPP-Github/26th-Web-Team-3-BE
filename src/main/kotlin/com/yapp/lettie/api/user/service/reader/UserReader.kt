package com.yapp.lettie.api.user.service.reader

import com.yapp.lettie.common.error.ErrorMessages
import com.yapp.lettie.common.exception.ApiErrorException
import com.yapp.lettie.domain.user.entity.User
import com.yapp.lettie.domain.user.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserReader(
    private val userRepository: UserRepository,
) {
    @Transactional(readOnly = true)
    fun getById(id: Long): User {
        return findById(id) ?: throw ApiErrorException(ErrorMessages.USER_NOT_FOUND)
    }

    @Transactional(readOnly = true)
    fun findById(id: Long): User? {
        return userRepository.findByIdOrNull(id)
    }
}

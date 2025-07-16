package com.yapp.lettie.api.timecapsule.service.reader

import com.yapp.lettie.common.error.ErrorMessages
import com.yapp.lettie.common.exception.ApiErrorException
import com.yapp.lettie.domain.timecapsule.entity.TimeCapsule
import com.yapp.lettie.domain.timecapsule.repository.TimeCapsuleRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TimeCapsuleReader(
    private val timeCapsuleRepository: TimeCapsuleRepository,
) {
    @Transactional(readOnly = true)
    fun getById(id: Long): TimeCapsule {
        return findById(id) ?: throw ApiErrorException(ErrorMessages.CAPSULE_NOT_FOUND)
    }

    @Transactional(readOnly = true)
    fun findById(id: Long): TimeCapsule? {
        return timeCapsuleRepository.findByIdOrNull(id)
    }
}

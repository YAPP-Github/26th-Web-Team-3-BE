package com.yapp.lettie.api.timecapsule.service.reader

import com.yapp.lettie.common.error.ErrorMessages
import com.yapp.lettie.common.exception.ApiErrorException
import com.yapp.lettie.domain.timecapsule.entity.TimeCapsule
import com.yapp.lettie.domain.timecapsule.repository.TimeCapsuleRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

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

    @Transactional(readOnly = true)
    fun getMyTimeCapsules(
        creatorId: Long,
        pageable: Pageable,
    ): List<TimeCapsule> {
        return timeCapsuleRepository.findByCreatorIdOrderByCreatedAtDesc(creatorId, pageable)
            ?: throw ApiErrorException(ErrorMessages.CAPSULE_NOT_FOUND)
    }

    @Transactional(readOnly = true)
    fun getPopularTimeCapsules(pageable: Pageable): List<TimeCapsule> {
        return timeCapsuleRepository.findPopularTimeCapsules(pageable)
    }
}

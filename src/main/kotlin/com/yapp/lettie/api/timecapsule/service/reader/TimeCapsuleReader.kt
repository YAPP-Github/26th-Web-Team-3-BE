package com.yapp.lettie.api.timecapsule.service.reader

import com.yapp.lettie.common.error.ErrorMessages
import com.yapp.lettie.common.exception.ApiErrorException
import com.yapp.lettie.domain.timecapsule.entity.TimeCapsule
import com.yapp.lettie.domain.timecapsule.entity.vo.AccessType
import com.yapp.lettie.domain.timecapsule.entity.vo.TimeCapsuleStatus
import com.yapp.lettie.domain.timecapsule.repository.TimeCapsuleRepository
import org.springframework.data.domain.Page
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
    fun getById(id: Long): TimeCapsule = findById(id) ?: throw ApiErrorException(ErrorMessages.CAPSULE_NOT_FOUND)

    @Transactional(readOnly = true)
    fun findById(id: Long): TimeCapsule? = timeCapsuleRepository.findByIdOrNull(id)

    @Transactional(readOnly = true)
    fun getMyTimeCapsules(
        creatorId: Long,
        pageable: Pageable,
    ): Page<TimeCapsule> =
        timeCapsuleRepository
            .findByCreatorIdOrderByCreatedAtDesc(creatorId, pageable)

    @Transactional(readOnly = true)
    fun getPopularTimeCapsules(pageable: Pageable): Page<TimeCapsule> =
        timeCapsuleRepository.findPopularTimeCapsules(pageable)

    @Transactional(readOnly = true)
    fun exploreTimeCapsules(
        timeCapsuleStatus: TimeCapsuleStatus?,
        now: LocalDateTime,
        pageable: Pageable,
    ): Page<TimeCapsule> =
        timeCapsuleRepository
            .getTimeCapsulesByStatus(timeCapsuleStatus, now, pageable)

    @Transactional(readOnly = true)
    fun findCapsulesToOpen(
        previousCheckTime: LocalDateTime,
        now: LocalDateTime,
    ): List<TimeCapsule> = timeCapsuleRepository.findAllCapsulesToOpen(previousCheckTime, now)

    @Transactional(readOnly = true)
    fun searchTimeCapsules(
        keyword: String,
        pageable: Pageable,
    ): Page<TimeCapsule> = timeCapsuleRepository.findTimeCapsulesByTitle(keyword, AccessType.PUBLIC, pageable)
}

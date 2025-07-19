package com.yapp.lettie.api.timecapsule.service.reader

import com.yapp.lettie.domain.timecapsule.repository.TimeCapsuleUserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TimeCapsuleUserReader(
    private val timeCapsuleUserRepository: TimeCapsuleUserRepository,
) {
    @Transactional(readOnly = true)
    fun getParticipantCount(capsuleId: Long): Long {
        return timeCapsuleUserRepository.countByTimeCapsuleId(capsuleId)
    }
}

package com.yapp.lettie.api.timecapsule.service.writer

import com.yapp.lettie.domain.timecapsule.entity.TimeCapsule
import com.yapp.lettie.domain.timecapsule.repository.TimeCapsuleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TimeCapsuleWriter(
    private val timeCapsuleRepository: TimeCapsuleRepository,
) {
    @Transactional
    fun save(timeCapsule: TimeCapsule): TimeCapsule {
        return timeCapsuleRepository.save(timeCapsule)
    }
}

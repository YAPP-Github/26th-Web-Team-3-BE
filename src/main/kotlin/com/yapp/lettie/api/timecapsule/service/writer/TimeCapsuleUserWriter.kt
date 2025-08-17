package com.yapp.lettie.api.timecapsule.service.writer

import com.yapp.lettie.domain.timecapsule.entity.TimeCapsuleUser
import com.yapp.lettie.domain.timecapsule.repository.TimeCapsuleUserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TimeCapsuleUserWriter(
    private val timeCapsuleUserRepository: TimeCapsuleUserRepository,
) {
    @Transactional
    fun save(timeCapsuleUser: TimeCapsuleUser) {
        timeCapsuleUserRepository.save(timeCapsuleUser)
    }
}

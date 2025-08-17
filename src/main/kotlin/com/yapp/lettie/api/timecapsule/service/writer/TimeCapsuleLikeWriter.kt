package com.yapp.lettie.api.timecapsule.service.writer

import com.yapp.lettie.domain.timecapsule.entity.TimeCapsuleLike
import com.yapp.lettie.domain.timecapsule.repository.TimeCapsuleLikeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TimeCapsuleLikeWriter(
    private val timeCapsuleLikeRepository: TimeCapsuleLikeRepository,
) {
    @Transactional
    fun save(like: TimeCapsuleLike): TimeCapsuleLike {
        return timeCapsuleLikeRepository.save(like)
    }
}

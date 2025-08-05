package com.yapp.lettie.api.timecapsule.service.reader

import com.yapp.lettie.domain.timecapsule.repository.TimeCapsuleUserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TimeCapsuleUserReader(
    private val timeCapsuleUserRepository: TimeCapsuleUserRepository,
) {
    @Transactional(readOnly = true)
    fun getParticipantCount(capsuleId: Long): Int {
        return timeCapsuleUserRepository.countByTimeCapsuleId(capsuleId)
    }

    @Transactional(readOnly = true)
    fun getParticipantCountMap(capsuleIds: List<Long>): Map<Long, Int> {
        return timeCapsuleUserRepository.getCountGroupedByCapsuleIds(capsuleIds)
            .associate { row ->
                val capsuleId = row[0] as Long
                val count = (row[1] as Long).toInt()
                capsuleId to count
            }
    }

    @Transactional(readOnly = true)
    fun findEmailsByCapsuleId(capsuleId: Long): List<String> =
        timeCapsuleUserRepository.findAllByTimeCapsuleId(capsuleId)
            .map { it.user.email }

    @Transactional(readOnly = true)
    fun getEmailsGroupByCapsuleId(capsuleIds: List<Long>): Map<Long, List<String>> =
        timeCapsuleUserRepository.findAllByCapsuleIdsFetchUser(capsuleIds)
            .groupBy({ it.timeCapsule.id }, { it.user.email })

    @Transactional(readOnly = true)
    fun hasUserJoinedCapsule(
        userId: Long,
        capsuleId: Long,
    ): Boolean {
        return timeCapsuleUserRepository.existsByUserIdAndTimeCapsuleId(userId, capsuleId)
    }
}

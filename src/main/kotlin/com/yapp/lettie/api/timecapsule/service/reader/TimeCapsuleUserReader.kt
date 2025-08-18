package com.yapp.lettie.api.timecapsule.service.reader

import com.yapp.lettie.common.error.ErrorMessages
import com.yapp.lettie.common.exception.ApiErrorException
import com.yapp.lettie.domain.timecapsule.dto.RecipientRow
import com.yapp.lettie.domain.timecapsule.entity.TimeCapsuleUser
import com.yapp.lettie.domain.timecapsule.entity.vo.TimeCapsuleUserStatus
import com.yapp.lettie.domain.timecapsule.repository.TimeCapsuleUserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TimeCapsuleUserReader(
    private val timeCapsuleUserRepository: TimeCapsuleUserRepository,
) {
    @Transactional(readOnly = true)
    fun getParticipantCount(capsuleId: Long): Int =
        timeCapsuleUserRepository.countByTimeCapsuleIdAndStatus(capsuleId, TimeCapsuleUserStatus.ACTIVE)

    @Transactional(readOnly = true)
    fun getParticipantCountMap(capsuleIds: List<Long>): Map<Long, Int> =
        timeCapsuleUserRepository
            .getCountGroupedByCapsuleIdsAndStatus(capsuleIds, TimeCapsuleUserStatus.ACTIVE)
            .associate { row ->
                val capsuleId = row[0] as Long
                val count = (row[1] as Long).toInt()
                capsuleId to count
            }

    @Transactional(readOnly = true)
    fun getRecipientsGroupedByCapsuleId(capsuleIds: List<Long>): Map<Long, List<RecipientRow>> {
        return timeCapsuleUserRepository
            .findRecipientsByCapsuleIds(capsuleIds)
            .groupBy { it.capsuleId }
    }

    @Transactional(readOnly = true)
    fun hasUserJoinedCapsule(
        userId: Long,
        capsuleId: Long,
    ): Boolean =
        timeCapsuleUserRepository.existsByUserIdAndTimeCapsuleIdAndStatus(
            userId,
            capsuleId,
            TimeCapsuleUserStatus.ACTIVE,
        )

    @Transactional(readOnly = true)
    fun getTimeCapsuleUser(
        capsuleId: Long,
        userId: Long,
    ): TimeCapsuleUser =
        timeCapsuleUserRepository.findByUserIdAndTimeCapsuleId(userId, capsuleId)
            ?: throw ApiErrorException(ErrorMessages.NOT_JOINED_TIME_CAPSULE)

    @Transactional(readOnly = true)
    fun getTimeCapsuleUserOrNull(
        capsuleId: Long,
        userId: Long,
    ): TimeCapsuleUser? = timeCapsuleUserRepository.findByUserIdAndTimeCapsuleId(userId, capsuleId)
}

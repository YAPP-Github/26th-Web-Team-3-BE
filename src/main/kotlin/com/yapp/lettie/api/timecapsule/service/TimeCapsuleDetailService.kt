package com.yapp.lettie.api.timecapsule.service

import com.yapp.lettie.api.timecapsule.service.dto.RemainingTimePayload
import com.yapp.lettie.api.timecapsule.service.dto.TimeCapsuleDetailPayload
import com.yapp.lettie.api.timecapsule.service.reader.TimeCapsuleLikeReader
import com.yapp.lettie.api.timecapsule.service.reader.TimeCapsuleReader
import com.yapp.lettie.api.user.service.reader.UserReader
import com.yapp.lettie.domain.timecapsule.entity.TimeCapsule
import com.yapp.lettie.domain.timecapsule.entity.vo.TimeCapsuleStatus
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime

@Service
class TimeCapsuleDetailService(
    private val userReader: UserReader,
    private val timeCapsuleReader: TimeCapsuleReader,
    private val timeCapsuleLikeReader: TimeCapsuleLikeReader,
) {
    fun getTimeCapsuleDetail(
        capsuleId: Long,
        userId: Long? = null,
    ): TimeCapsuleDetailPayload {
        val capsule = timeCapsuleReader.getById(capsuleId)
        val now = LocalDateTime.now()

        val liked =
            userId?.let {
                val user = userReader.getById(it)
                timeCapsuleLikeReader.findByUserAndCapsule(user, capsule)?.isLiked
            }
        val status = calculateStatus(now, capsule)
        val remainingTime = calculateRemainingTime(now, capsule, status)

        // TODO: 편지 몇 동있는지 추가
        return TimeCapsuleDetailPayload(
            id = capsule.id,
            title = capsule.title,
            subtitle = capsule.subtitle,
            openAt = capsule.openAt,
            participantCount = capsule.timeCapsuleUsers.size,
            likeCount = capsule.timeCapsuleLikes.count { it.isLiked },
            isLiked = liked,
            status = status,
            remainingTime = remainingTime,
        )
    }

    private fun calculateStatus(
        now: LocalDateTime,
        capsule: TimeCapsule,
    ): TimeCapsuleStatus {
        return when {
            now.isBefore(capsule.closedAt) -> TimeCapsuleStatus.WRITABLE
            now.isBefore(capsule.openAt) -> TimeCapsuleStatus.WAITING_OPEN
            else -> TimeCapsuleStatus.OPENED
        }
    }

    private fun calculateRemainingTime(
        now: LocalDateTime,
        capsule: TimeCapsule,
        status: TimeCapsuleStatus,
    ): RemainingTimePayload {
        return when (status) {
            TimeCapsuleStatus.WRITABLE -> {
                val duration = Duration.between(now, capsule.closedAt)
                RemainingTimePayload(
                    days = duration.toDays(),
                    hours = duration.toHoursPart().toLong(),
                    minutes = duration.toMinutesPart().toLong(),
                )
            }

            TimeCapsuleStatus.WAITING_OPEN -> {
                val duration = Duration.between(now, capsule.openAt)
                RemainingTimePayload(
                    days = duration.toDays(),
                    hours = duration.toHoursPart().toLong(),
                    minutes = duration.toMinutesPart().toLong(),
                )
            }

            TimeCapsuleStatus.OPENED -> {
                RemainingTimePayload(openDate = capsule.openAt.toLocalDate())
            }
        }
    }
}

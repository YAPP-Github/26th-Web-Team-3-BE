package com.yapp.lettie.api.timecapsule.service

import com.yapp.lettie.api.timecapsule.service.dto.RemainingTimeDto
import com.yapp.lettie.api.timecapsule.service.dto.TimeCapsuleDetailDto
import com.yapp.lettie.api.timecapsule.service.reader.TimeCapsuleLikeReader
import com.yapp.lettie.api.timecapsule.service.reader.TimeCapsuleReader
import com.yapp.lettie.api.timecapsule.service.reader.TimeCapsuleUserReader
import com.yapp.lettie.domain.timecapsule.entity.vo.TimeCapsuleStatus
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime

@Service
class TimeCapsuleDetailService(
    private val timeCapsuleReader: TimeCapsuleReader,
    private val timeCapsuleLikeReader: TimeCapsuleLikeReader,
    private val timeCapsuleUserReader: TimeCapsuleUserReader,
) {
    fun getTimeCapsuleDetail(
        capsuleId: Long,
        userId: Long,
    ): TimeCapsuleDetailDto {
        val capsule = timeCapsuleReader.getById(capsuleId)
        val now = LocalDateTime.now()

        val liked = timeCapsuleLikeReader.findByUserIdAndCapsuleId(userId, capsuleId)?.isLiked ?: false
        val status = capsule.getStatus(now)
        val remainingTime = calculateRemainingTime(status, now, capsule.openAt, capsule.closedAt)
        val likeCount = timeCapsuleLikeReader.getLikeCount(capsuleId)
        val participantCount = timeCapsuleUserReader.getParticipantCount(capsuleId)

        // TODO: 편지 몇 동있는지 추가
        return TimeCapsuleDetailDto(
            id = capsule.id,
            title = capsule.title,
            subtitle = capsule.subtitle,
            openAt = capsule.openAt,
            participantCount = participantCount,
            likeCount = likeCount,
            isLiked = liked,
            status = status,
            remainingTime = remainingTime,
        )
    }

    fun calculateRemainingTime(
        status: TimeCapsuleStatus,
        now: LocalDateTime,
        openAt: LocalDateTime,
        closedAt: LocalDateTime,
    ): RemainingTimeDto {
        return when (status) {
            TimeCapsuleStatus.WRITABLE -> {
                val duration = Duration.between(now, closedAt)
                RemainingTimeDto(
                    days = duration.toDays(),
                    hours = duration.toHoursPart().toLong(),
                    minutes = duration.toMinutesPart().toLong(),
                )
            }

            TimeCapsuleStatus.WAITING_OPEN -> {
                val duration = Duration.between(now, openAt)
                RemainingTimeDto(
                    days = duration.toDays(),
                    hours = duration.toHoursPart().toLong(),
                    minutes = duration.toMinutesPart().toLong(),
                )
            }

            TimeCapsuleStatus.OPENED -> {
                RemainingTimeDto(openDate = openAt.toLocalDate())
            }
        }
    }
}

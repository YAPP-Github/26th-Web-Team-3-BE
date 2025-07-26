package com.yapp.lettie.api.timecapsule.service

import com.yapp.lettie.api.letter.service.reader.LetterReader
import com.yapp.lettie.api.timecapsule.service.dto.RemainingTimeDto
import com.yapp.lettie.api.timecapsule.service.dto.TimeCapsuleDetailDto
import com.yapp.lettie.api.timecapsule.service.dto.TimeCapsuleSummaryDto
import com.yapp.lettie.api.timecapsule.service.reader.TimeCapsuleLikeReader
import com.yapp.lettie.api.timecapsule.service.reader.TimeCapsuleReader
import com.yapp.lettie.api.timecapsule.service.reader.TimeCapsuleUserReader
import com.yapp.lettie.domain.timecapsule.entity.vo.TimeCapsuleStatus
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime

@Service
class TimeCapsuleDetailService(
    private val timeCapsuleReader: TimeCapsuleReader,
    private val timeCapsuleLikeReader: TimeCapsuleLikeReader,
    private val timeCapsuleUserReader: TimeCapsuleUserReader,
    private val letterReader: LetterReader,
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
        val letterCount = letterReader.getLetterCountByCapsuleId(capsule.id)

        // TODO: 편지 몇 동있는지 추가
        return TimeCapsuleDetailDto(
            id = capsule.id,
            title = capsule.title,
            subtitle = capsule.subtitle,
            openAt = capsule.openAt,
            participantCount = participantCount,
            letterCount = letterCount,
            likeCount = likeCount,
            isLiked = liked,
            status = status,
            remainingTime = remainingTime,
        )
    }

    fun getMyTimeCapsules(
        userId: Long,
        limit: Int,
    ): List<TimeCapsuleSummaryDto> {
        val pageable = PageRequest.of(0, limit)
        val capsules = timeCapsuleReader.getMyTimeCapsules(userId, pageable)
        val now = LocalDateTime.now()

        return capsules.map { capsule ->
            val participantCount = timeCapsuleUserReader.getParticipantCount(capsule.id)
            val letterCount = letterReader.getLetterCountByCapsuleId(capsule.id)
            val remainingStatus = getRemainingStatus(capsule.openAt, now)

            TimeCapsuleSummaryDto(
                id = capsule.id,
                title = capsule.title,
                participantCount = participantCount,
                letterCount = letterCount,
                remainingStatus = remainingStatus,
            )
        }
    }

    fun getPopularTimeCapsules(limit: Int): List<TimeCapsuleSummaryDto> {
        val pageable = PageRequest.of(0, limit)
        val capsules = timeCapsuleReader.getPopularTimeCapsules(pageable)

        return capsules.map { capsule ->
            val letterCount = letterReader.getLetterCountByCapsuleId(capsule.id)
            TimeCapsuleSummaryDto(
                id = capsule.id,
                title = capsule.title,
                participantCount = timeCapsuleUserReader.getParticipantCount(capsule.id),
                letterCount = letterCount,
                remainingStatus = getRemainingStatus(capsule.openAt, LocalDateTime.now()),
            )
        }
    }

    private fun calculateRemainingTime(
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

    private fun getRemainingStatus(
        openAt: LocalDateTime,
        now: LocalDateTime,
    ): String {
        return if (now.isBefore(openAt)) {
            val daysLeft =
                Duration.between(
                    now.toLocalDate().atStartOfDay(),
                    openAt.toLocalDate().atStartOfDay(),
                ).toDays()
            "D-$daysLeft"
        } else {
            "오픈 완료"
        }
    }
}

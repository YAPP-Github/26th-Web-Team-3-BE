package com.yapp.lettie.api.timecapsule.service.dto

import com.yapp.lettie.domain.timecapsule.entity.vo.TimeCapsuleStatus
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

data class RemainingTimeDto(
    val days: Long? = null,
    val hours: Long? = null,
    val minutes: Long? = null,
    val openDate: LocalDate? = null,
) {
    companion object {
        fun fromStatus(
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
}

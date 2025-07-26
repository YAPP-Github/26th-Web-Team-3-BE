package com.yapp.lettie.api.timecapsule.service.dto

import java.time.Duration
import java.time.LocalDateTime
import io.swagger.v3.oas.annotations.media.Schema


@Schema(description = "캡슐 상태 정보")
data class RemainingStatusDto(
    @Schema(description = "캡슐 상태 타입", example = "COUNTDOWN")
    val type: StatusType,

    @Schema(description = "남은 시간 정보 (type이 COUNTDOWN일 경우 존재)")
    val remainingTime: RemainingTimeDto? = null,

    @Schema(description = "오픈일 (type이 OPENED일 경우 존재)", example = "2025-12-31T00:00:00")
    val openDate: LocalDateTime? = null,

    @Schema(description = "추가 메시지", example = "오픈 완료")
    val message: String? = null,
) {
    @Schema(description = "캡슐 상태 타입", example = "OPENED")
    enum class StatusType {
        COUNTDOWN,
        OPENED
    }

    companion object {
        fun of(openAt: LocalDateTime, now: LocalDateTime): RemainingStatusDto {
            return if (now.isBefore(openAt)) {
                val duration = Duration.between(now, openAt)
                RemainingStatusDto(
                    type = StatusType.COUNTDOWN,
                    remainingTime = RemainingTimeDto(
                        days = duration.toDays(),
                        hours = duration.toHoursPart().toLong(),
                        minutes = duration.toMinutesPart().toLong()
                    )
                )
            } else {
                RemainingStatusDto(
                    type = StatusType.OPENED,
                    openDate = openAt,
                    message = "오픈 완료"
                )
            }
        }
    }
}

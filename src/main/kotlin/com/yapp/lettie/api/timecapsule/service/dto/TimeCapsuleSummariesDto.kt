package com.yapp.lettie.api.timecapsule.service.dto

import com.yapp.lettie.domain.timecapsule.entity.TimeCapsule
import org.springframework.data.domain.Page
import java.time.LocalDateTime

data class TimeCapsuleSummariesDto(
    val timeCapsules: List<TimeCapsuleSummaryDto>,
    val size: Int,
    val totalPages: Int,
    val page: Int,
    val totalCount: Long,
) {
    companion object {
        fun of(
            capsules: Page<TimeCapsule>,
            participantCountMap: Map<Long, Int>,
            letterCountMap: Map<Long, Int>,
            now: LocalDateTime,
        ): TimeCapsuleSummariesDto =
            TimeCapsuleSummariesDto(
                capsules.content.map { capsule ->
                    TimeCapsuleSummaryDto(
                        id = capsule.id,
                        title = capsule.title,
                        participantCount = participantCountMap[capsule.id] ?: 0,
                        letterCount = letterCountMap[capsule.id] ?: 0,
                        remainingStatus = RemainingStatusDto.of(capsule.openAt, now, capsule.getStatus(now)),
                    )
                },
                size = capsules.size,
                totalPages = capsules.totalPages,
                page = capsules.number,
                totalCount = capsules.totalElements,
            )
    }
}

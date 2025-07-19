package com.yapp.lettie.api.timecapsule.controller.response

import com.yapp.lettie.api.timecapsule.service.dto.TimeCapsuleDetailDto
import com.yapp.lettie.domain.timecapsule.entity.vo.TimeCapsuleStatus
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "타임캡슐 상세 응답")
data class TimeCapsuleDetailResponse(
    @Schema(description = "타임캡슐 ID", example = "123")
    val id: Long,
    @Schema(description = "제목", example = "비 오는 날의 타임캡슐")
    val title: String,
    @Schema(description = "내용", example = "비 오는 날에만 꺼내보고 싶은 이야기")
    val subtitle: String?,
    @Schema(description = "오픈 시각", example = "2025-07-01T13:00:00")
    val openAt: LocalDateTime,
    @Schema(description = "참여자 수", example = "8")
    val participantCount: Int,
    @Schema(description = "좋아요 수", example = "31L")
    val likeCount: Long,
    @Schema(description = "좋아요 여부 (로그인 시에만 포함)", example = "true")
    val isLiked: Boolean? = false,
    @Schema(description = "캡슐 상태", example = "WRITABLE")
    val status: TimeCapsuleStatus,
    @Schema(description = "남은 시간 정보 또는 오픈 날짜")
    val remainingTime: RemainingTimeResponse?,
) {
    companion object {
        fun from(dto: TimeCapsuleDetailDto): TimeCapsuleDetailResponse {
            return TimeCapsuleDetailResponse(
                id = dto.id,
                title = dto.title,
                subtitle = dto.subtitle,
                openAt = dto.openAt,
                participantCount = dto.participantCount,
                likeCount = dto.likeCount,
                isLiked = dto.isLiked,
                status = dto.status,
                remainingTime =
                    dto.remainingTime?.let {
                        RemainingTimeResponse(
                            days = it.days,
                            hours = it.hours,
                            minutes = it.minutes,
                            openDate = it.openDate,
                        )
                    },
            )
        }
    }
}

package com.yapp.lettie.api.timecapsule.controller.response

import com.yapp.lettie.api.timecapsule.service.dto.RemainingStatusDto
import com.yapp.lettie.api.timecapsule.service.dto.TimeCapsuleSummaryDto
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "타임캡슐 요약 응답")
data class TimeCapsuleSummaryResponse(
    @Schema(description = "타임캡슐 ID", example = "1")
    val id: Long,
    @Schema(description = "타임캡슐 고유 식별자(공유 링크용)", example = "abc123xy")
    val inviteCode: String,
    @Schema(description = "타임캡슐 제목", example = "2025 새해 타임캡슐")
    val title: String,
    @Schema(description = "참여 인원 수", example = "4")
    val participantCount: Int,
    @Schema(description = "작성된 편지 수", example = "6")
    val letterCount: Int,
    @Schema(
        description = "캡슐 오픈까지 남은 시간 또는 오픈 상태",
        implementation = RemainingStatusDto::class,
    )
    val remainingStatus: RemainingStatusDto,
    // TODO: 썸네일 이미지 objectkey 추가
) {
    companion object {
        fun from(dto: TimeCapsuleSummaryDto): TimeCapsuleSummaryResponse =
            TimeCapsuleSummaryResponse(
                id = dto.id,
                inviteCode = dto.inviteCode,
                title = dto.title,
                participantCount = dto.participantCount,
                letterCount = dto.letterCount,
                remainingStatus = dto.remainingStatus,
            )
    }
}

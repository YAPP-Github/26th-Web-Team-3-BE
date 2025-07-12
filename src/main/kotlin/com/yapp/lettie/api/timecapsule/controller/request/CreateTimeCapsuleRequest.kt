package com.yapp.lettie.api.timecapsule.controller.request

import com.yapp.lettie.api.timecapsule.service.dto.CreateTimeCapsulePayload
import com.yapp.lettie.domain.timecapsule.entity.vo.AccessType
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "타임캡슐 생성 요청")
data class CreateTimeCapsuleRequest(
    @Schema(description = "제목", example = "졸업 타임캡슐")
    val title: String,
    @Schema(description = "부제목", example = "우리의 마지막 추억")
    val subtitle: String? = null,
    @Schema(description = "공개 범위", example = "PUBLIC or PRIVATE")
    val accessType: AccessType,
    @Schema(description = "캡슐 오픈 시점", example = "2025-12-01T00:00:00")
    val openAt: LocalDateTime,
    @Schema(description = "캡슐 작성 마감 시점", example = "2025-12-31T23:59:59")
    val closedAt: LocalDateTime,
) {
    fun to(): CreateTimeCapsulePayload {
        return CreateTimeCapsulePayload(
            title = this.title,
            subtitle = this.subtitle,
            accessType = this.accessType,
            openAt = this.openAt,
            closedAt = this.closedAt,
        )
    }
}

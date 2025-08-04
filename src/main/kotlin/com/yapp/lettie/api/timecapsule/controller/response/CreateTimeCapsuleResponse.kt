package com.yapp.lettie.api.timecapsule.controller.response

import com.yapp.lettie.api.timecapsule.service.dto.CreateTimeCapsuleDto
import io.swagger.v3.oas.annotations.media.Schema

data class CreateTimeCapsuleResponse(
    @Schema(description = "timeCapsule Id", example = "1L")
    val id: Long,
    @Schema(description = "타임캡슐 고유 식별자(공유 링크용)", example = "abc123xy")
    val inviteCode: String,
) {
    companion object {
        fun from(dto: CreateTimeCapsuleDto): CreateTimeCapsuleResponse {
            return CreateTimeCapsuleResponse(
                id = dto.id,
                inviteCode = dto.inviteCode,
            )
        }
    }
}

package com.yapp.lettie.api.timecapsule.controller.response

import io.swagger.v3.oas.annotations.media.Schema

data class CreateTimeCapsuleResponse(
    @Schema(description = "timeCapsule Id", example = "1L")
    val id: Long,
)

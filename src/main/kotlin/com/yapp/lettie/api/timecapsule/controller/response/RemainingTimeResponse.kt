package com.yapp.lettie.api.timecapsule.controller.response

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema(description = "남은 시간 또는 오픈 날짜 정보")
data class RemainingTimeResponse(
    @Schema(description = "남은 일 수", example = "2", nullable = true)
    val days: Long? = null,
    @Schema(description = "남은 시간", example = "10", nullable = true)
    val hours: Long? = null,
    @Schema(description = "남은 분", example = "30", nullable = true)
    val minutes: Long? = null,
    @Schema(description = "오픈된 날짜 (상태가 OPENED일 경우만 존재)", example = "2025-07-01", nullable = true)
    val openDate: LocalDate? = null,
)

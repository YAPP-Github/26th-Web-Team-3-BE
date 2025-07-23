package com.yapp.lettie.api.file.controller.response

import com.yapp.lettie.api.file.service.dto.PresignedUrlDto
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

data class PresignedUrlResponse(
    @Schema(description = "PreSigned URL", example = "https://example.com/presigned-url")
    val presignedUrl: String,
    @Schema(description = "객체 키", example = "LETTER/20250722/LETTER__20250722000339251.png")
    val objectKey: String,
    @Schema(description = "만료 시간", example = "2023-10-01T12:00:00", format = "yyyy-MM-dd HH:mm:ss")
    val expiresAt: LocalDateTime,
) {
    companion object {
        fun of(presignedUrl: PresignedUrlDto): PresignedUrlResponse =
            PresignedUrlResponse(
                presignedUrl = presignedUrl.url,
                objectKey = presignedUrl.key,
                expiresAt = presignedUrl.expireAt,
            )
    }
}

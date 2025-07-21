package com.yapp.lettie.api.file.controller.response

import com.yapp.lettie.api.file.service.dto.PresignedUrlDto
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class PresignedUrlResponse(
    val presignedUrl: String,
    val objectKey: String,
    val expiresAt: String,
) {
    companion object {
        fun of(presignedUrl: PresignedUrlDto): PresignedUrlResponse =
            PresignedUrlResponse(
                presignedUrl = presignedUrl.url,
                objectKey = presignedUrl.key,
                expiresAt =
                    LocalDateTime
                        .now()
                        .plusMinutes(presignedUrl.expiryInMinutes.toLong())
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            )
    }
}

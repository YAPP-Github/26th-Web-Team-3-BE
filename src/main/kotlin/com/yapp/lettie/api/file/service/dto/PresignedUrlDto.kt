package com.yapp.lettie.api.file.service.dto

import java.time.LocalDateTime

data class PresignedUrlDto(
    val url: String,
    val key: String,
    val expireAt: LocalDateTime,
)

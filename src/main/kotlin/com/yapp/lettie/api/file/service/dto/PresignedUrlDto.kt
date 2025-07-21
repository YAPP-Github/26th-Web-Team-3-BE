package com.yapp.lettie.api.file.service.dto

data class PresignedUrlDto(
    val url: String,
    val key: String,
    val expiryInMinutes: Int,
)

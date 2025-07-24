package com.yapp.lettie.api.file.controller.request

data class PresignedUrlRequest(
    val fileName: String,
    val expiryInMinutes: Int = 60,
)

package com.yapp.lettie.api.letter.service.dto

data class CreateLetterPayload(
    val capsuleId: Long,
    val content: String,
    val objectKey: String?,
    val from: String?,
)

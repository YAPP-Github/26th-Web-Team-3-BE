package com.yapp.lettie.api.letter.controller.request

import com.yapp.lettie.api.timecapsule.service.dto.CreateLetterPayload

data class CreateLetterRequest(
    val capsuleId: Long,
    val content: String,
    val imageUrl: String?,
    val from: String?,
) {
    fun toPayload() =
        CreateLetterPayload(
            capsuleId = capsuleId,
            content = content,
            key = imageUrl,
            from = from,
        )
}

package com.yapp.lettie.api.letter.controller.request

import com.yapp.lettie.api.timecapsule.service.dto.CreateLetterPayload
import io.swagger.v3.oas.annotations.media.Schema

data class CreateLetterRequest(
    @Schema(description = "타임캡슐 ID", example = "1")
    val capsuleId: Long,
    @Schema(description = "편지 내용", example = "안녕하세요! 타임캡슐에 남기는 편지입니다.")
    val content: String,
    @Schema(description = "파일의 객체 키", example = "LETTER/20250722/LETTER__20250722000339251.png")
    val objectKey: String?,
    @Schema(description = "편지 작성자 이름", example = "홍길동")
    val from: String?,
) {
    fun toPayload() =
        CreateLetterPayload(
            capsuleId = capsuleId,
            content = content,
            objectKey = objectKey,
            from = from,
        )
}

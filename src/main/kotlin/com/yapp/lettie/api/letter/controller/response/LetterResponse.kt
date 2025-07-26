package com.yapp.lettie.api.letter.controller.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.yapp.lettie.api.file.controller.response.FileResponse
import com.yapp.lettie.api.letter.service.dto.LetterDto
import io.swagger.v3.oas.annotations.media.Schema

data class LetterResponse(
    @Schema(description = "letter Id", example = "1")
    @JsonProperty(value = "letterId")
    val id: Long,
    @Schema(description = "편지 내용", example = "안녕하세요! 타임캡슐에 남기는 편지입니다.")
    val content: String,
    @Schema(description = "from", example = "홍길동")
    val from: String?,
    @Schema(description = "이미지 파일", example = "1L")
    val files: List<FileResponse>,
    @Schema(description = "내가 작성한 편지 여부", example = "true")
    val isMine: Boolean,
) {
    companion object {
        fun of(letter: LetterDto): LetterResponse =
            LetterResponse(
                id = letter.id,
                content = letter.content,
                from = letter.from,
                files = letter.files.map { FileResponse.of(it) },
                isMine = letter.isMine,
            )
    }
}

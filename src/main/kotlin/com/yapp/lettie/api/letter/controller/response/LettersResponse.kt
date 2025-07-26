package com.yapp.lettie.api.letter.controller.response

import com.yapp.lettie.api.letter.service.dto.LettersDto
import io.swagger.v3.oas.annotations.media.Schema

data class LettersResponse(
    @Schema(description = "편지 목록")
    val letters: List<LetterResponse>,
    @Schema(description = "편지 목록의 크기", example = "10")
    val size: Int,
    @Schema(description = "전체 페이지 수", example = "5")
    val totalPages: Int,
    @Schema(description = "현재 페이지 번호", example = "1")
    val page: Int,
    @Schema(description = "전체 편지 수", example = "50")
    val totalElements: Long,
) {
    companion object {
        fun of(letters: LettersDto): LettersResponse =
            LettersResponse(
                letters = letters.letters.map { LetterResponse.of(it) },
                size = letters.size,
                totalPages = letters.totalPages,
                page = letters.page,
                totalElements = letters.totalCount,
            )
    }
}

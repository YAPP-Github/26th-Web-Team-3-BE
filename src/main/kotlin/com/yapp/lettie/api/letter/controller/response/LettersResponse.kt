package com.yapp.lettie.api.letter.controller.response

import com.yapp.lettie.api.letter.service.dto.LettersDto

data class LettersResponse(
    val letters: List<LetterResponse>,
    val size: Int,
    val totalPages: Int,
    val totalElements: Long,
) {
    companion object {
        fun of(letters: LettersDto): LettersResponse =
            LettersResponse(
                letters = letters.letters.map { LetterResponse.of(it) },
                size = letters.size,
                totalPages = letters.totalPages,
                totalElements = letters.totalCount,
            )
    }
}

package com.yapp.lettie.api.letter.service.dto

import com.yapp.lettie.domain.letter.entity.Letter
import org.springframework.data.domain.Page

data class LettersDto(
    val letters: List<LetterDto>,
    val size: Int,
    val totalPages: Int,
    val page: Int,
    val totalCount: Long,
) {
    companion object {
        fun of(
            userId: Long,
            letters: Page<Letter>,
        ): LettersDto =
            LettersDto(
                letters = letters.content.map { LetterDto.of(userId, it) },
                size = letters.size,
                totalPages = letters.totalPages,
                page = letters.number,
                totalCount = letters.totalElements,
            )
    }
}

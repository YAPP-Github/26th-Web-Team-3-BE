package com.yapp.lettie.api.letter.service.dto

import com.yapp.lettie.api.file.service.dto.FileDto
import com.yapp.lettie.domain.letter.entity.Letter

data class LetterDto(
    val id: Long,
    val content: String,
    val files: List<FileDto>,
    val from: String?,
    val isMine: Boolean,
) {
    companion object {
        fun of(
            userId: Long,
            letter: Letter,
        ): LetterDto =
            LetterDto(
                id = letter.id,
                content = letter.content,
                files = letter.letterFiles.map { FileDto.of(it) },
                from = letter.from,
                isMine = letter.isMine(userId),
            )
    }
}

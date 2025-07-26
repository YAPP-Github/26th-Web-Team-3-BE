package com.yapp.lettie.api.letter.controller.response

import com.yapp.lettie.api.file.controller.response.FileResponse
import com.yapp.lettie.api.letter.service.dto.LetterDto

data class LetterResponse(
    val id: Long,
    val content: String,
    val from: String?,
    val files: List<FileResponse>,
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

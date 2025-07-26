package com.yapp.lettie.api.file.service.dto

import com.yapp.lettie.domain.file.entity.LetterFile

data class FileDto(
    val id: Long,
    val objectKey: String,
) {
    companion object {
        fun of(file: LetterFile): FileDto = FileDto(id = file.id, objectKey = file.objectKey)
    }
}

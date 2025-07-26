package com.yapp.lettie.api.file.controller.response

import com.yapp.lettie.api.file.service.dto.FileDto

data class FileResponse(
    val id: Long,
    val objectKey: String,
) {
    companion object {
        fun of(file: FileDto): FileResponse = FileResponse(id = file.id, objectKey = file.objectKey)
    }
}

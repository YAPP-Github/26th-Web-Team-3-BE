package com.yapp.lettie.api.file.controller.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.yapp.lettie.api.file.service.dto.FileDto
import io.swagger.v3.oas.annotations.media.Schema

data class FileResponse(
    @Schema(description = "파일 ID", example = "1L")
    @JsonProperty(value = "fileId")
    val id: Long,
    @Schema(description = "파일의 객체 키", example = "LETTER/20250722/LETTER__20250722000339251.png")
    val objectKey: String,
) {
    companion object {
        fun of(file: FileDto): FileResponse = FileResponse(id = file.id, objectKey = file.objectKey)
    }
}

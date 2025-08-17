package com.yapp.lettie.api.file.controller

import com.yapp.lettie.api.file.controller.response.PresignedUrlResponse
import com.yapp.lettie.api.file.controller.swagger.FileSwagger
import com.yapp.lettie.api.file.service.FileService
import com.yapp.lettie.domain.file.FileType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/files")
class FileController(
    private val fileService: FileService,
) : FileSwagger {
    @GetMapping("/presigned-url")
    override fun generateUploadUrl(
        @RequestParam(value = "fileName") fileName: FileType,
        @RequestParam(value = "extension") extension: String,
    ): ResponseEntity<PresignedUrlResponse> {
        val presignedUrl =
            fileService.getPresignedUploadUrl(
                fileName,
                extension,
            )

        return ResponseEntity.ok(
            PresignedUrlResponse.of(presignedUrl),
        )
    }

    @GetMapping("{file-id}/presigned-url")
    override fun generateDownloadUrl(
        @PathVariable("file-id") fileId: Long,
    ): ResponseEntity<PresignedUrlResponse> {
        val presignedUrl =
            fileService.generatePresignedDownloadUrl(
                fileId = fileId,
            )

        return ResponseEntity.ok(
            PresignedUrlResponse.of(presignedUrl),
        )
    }
}

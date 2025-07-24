package com.yapp.lettie.api.file.controller.swagger

import com.yapp.lettie.api.file.controller.response.PresignedUrlResponse
import com.yapp.lettie.domain.file.FileType
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity

@Tag(name = "TimeCapsule Detail", description = "타임캡슐 Detail API (상세보기, 리스트 목록 반환)")
interface FileSwagger {
    @Operation(summary = "파일 업로드용 Presigned URL 생성", description = "PUT 방식으로 파일을 직접 업로드할 수 있는 presigned URL을 생성합니다.")
    fun generateUploadUrl(
        fileName: FileType,
        extension: String,
    ): ResponseEntity<PresignedUrlResponse>

    @Operation(summary = "파일 다운로드용 Presigned URL 생성", description = "MinIO에 저장된 파일을 다운로드할 수 있는 presigned URL을 생성합니다.")
    fun generateDownloadUrl(fileId: Long): ResponseEntity<PresignedUrlResponse>
}

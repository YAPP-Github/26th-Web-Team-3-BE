package com.yapp.lettie.api.file.service

import com.yapp.lettie.api.file.service.dto.PresignedUrlDto
import com.yapp.lettie.api.file.service.reader.FileReader
import com.yapp.lettie.common.error.ErrorMessages
import com.yapp.lettie.common.exception.ApiErrorException
import com.yapp.lettie.domain.file.FileType
import com.yapp.lettie.infrastructure.minio.MinioProperties
import io.minio.GetPresignedObjectUrlArgs
import io.minio.MinioClient
import io.minio.http.Method
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

@Service
class FileService(
    private val minioClient: MinioClient,
    private val minioProperties: MinioProperties,
    private val fileReader: FileReader,
) {
    companion object {
        const val EXPIRY_IN_MINUTES = 5
    }

    fun getPresignedUploadUrl(
        fileType: FileType,
        extension: String,
    ): PresignedUrlDto {
        try {
            val key = generateUniqueFileName(fileType, extension)

            val presignedUrl =
                minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs
                        .builder()
                        .method(Method.PUT)
                        .bucket(minioProperties.bucketName)
                        .`object`(key)
                        .expiry(EXPIRY_IN_MINUTES, TimeUnit.MINUTES)
                        .build(),
                )

            return PresignedUrlDto(presignedUrl, key, EXPIRY_IN_MINUTES)
        } catch (e: Exception) {
            throw ApiErrorException(ErrorMessages.CAN_NOT_GET_PRESIGNED_URL)
        }
    }

    fun generatePresignedDownloadUrl(fileId: Long): PresignedUrlDto {
        val file = fileReader.getById(fileId)

        try {
            val presignedUrl =
                minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs
                        .builder()
                        .method(Method.GET)
                        .bucket(minioProperties.bucketName)
                        .`object`(file.objectKey)
                        .expiry(EXPIRY_IN_MINUTES, TimeUnit.MINUTES)
                        .build(),
                )

            return PresignedUrlDto(presignedUrl, file.objectKey, EXPIRY_IN_MINUTES)
        } catch (e: Exception) {
            throw ApiErrorException(ErrorMessages.CAN_NOT_GET_PRESIGNED_URL)
        }
    }

    private fun generateUniqueFileName(
        fileType: FileType,
        extension: String,
    ): String {
        val now = LocalDateTime.now()
        val today = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))

        return "$today/${fileType.name}__$timestamp.$extension"
    }
}

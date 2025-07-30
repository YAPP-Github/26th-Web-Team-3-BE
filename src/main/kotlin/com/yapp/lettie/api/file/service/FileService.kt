package com.yapp.lettie.api.file.service

import com.yapp.lettie.api.file.service.dto.PresignedUrlDto
import com.yapp.lettie.api.file.service.reader.FileReader
import com.yapp.lettie.common.error.ErrorMessages
import com.yapp.lettie.common.exception.ApiErrorException
import com.yapp.lettie.domain.file.FileType
import com.yapp.lettie.infrastructure.minio.MinioProperties
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class FileService(
    private val s3Presigner: S3Presigner,
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
            val expiresAt = LocalDateTime.now().plusMinutes(EXPIRY_IN_MINUTES.toLong())

            val putObjectRequest =
                PutObjectRequest
                    .builder()
                    .bucket(minioProperties.bucketName)
                    .key(key)
                    .build()

            val presignRequest =
                PutObjectPresignRequest
                    .builder()
                    .signatureDuration(Duration.ofMinutes(EXPIRY_IN_MINUTES.toLong()))
                    .putObjectRequest(putObjectRequest)
                    .build()

            val presigned: PresignedPutObjectRequest = s3Presigner.presignPutObject(presignRequest)

            return PresignedUrlDto(presigned.url().toString(), key, expiresAt)
        } catch (e: Exception) {
            throw ApiErrorException(ErrorMessages.CAN_NOT_GET_PRESIGNED_URL)
        }
    }

    fun generatePresignedDownloadUrl(fileId: Long): PresignedUrlDto {
        val file = fileReader.getById(fileId)

        try {
            val expiresAt = LocalDateTime.now().plusMinutes(EXPIRY_IN_MINUTES.toLong())

            val getObjectRequest =
                GetObjectRequest
                    .builder()
                    .bucket(minioProperties.bucketName)
                    .key(file.objectKey)
                    .build()

            val presignRequest =
                GetObjectPresignRequest
                    .builder()
                    .signatureDuration(Duration.ofMinutes(EXPIRY_IN_MINUTES.toLong()))
                    .getObjectRequest(getObjectRequest)
                    .build()

            val presigned: PresignedGetObjectRequest = s3Presigner.presignGetObject(presignRequest)

            return PresignedUrlDto(presigned.url().toString(), file.objectKey, expiresAt)
        } catch (e: Exception) {
            throw ApiErrorException(ErrorMessages.CAN_NOT_GET_PRESIGNED_URL)
        }
    }

    fun generatePresignedDownloadUrlByObjectKey(objectKey: String): PresignedUrlDto {
        try {
            val expiresAt = LocalDateTime.now().plusMinutes(EXPIRY_IN_MINUTES.toLong())

            val getObjectRequest = GetObjectRequest.builder()
                .bucket(minioProperties.bucketName)
                .key(objectKey)
                .build()

            val presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(EXPIRY_IN_MINUTES.toLong()))
                .getObjectRequest(getObjectRequest)
                .build()

            val presigned = s3Presigner.presignGetObject(presignRequest)

            return PresignedUrlDto(presigned.url().toString(), objectKey, expiresAt)
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

        return "${fileType.name}/$today/${fileType.name}__$timestamp.$extension"
    }
}

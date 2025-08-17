package com.yapp.lettie.config

import com.yapp.lettie.infrastructure.minio.MinioProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.S3Configuration
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import java.net.URI

@Configuration
@EnableConfigurationProperties(MinioProperties::class)
class S3Config(
    private val minioProperties: MinioProperties,
) {
    @Bean
    fun s3Client(): S3Client =
        S3Client
            .builder()
            .endpointOverride(URI.create(minioProperties.endpoint)) // MinIO endpoint
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        minioProperties.accessKey,
                        minioProperties.secretKey,
                    ),
                ),
            ).region(Region.AP_NORTHEAST_2)
            .serviceConfiguration(
                S3Configuration
                    .builder()
                    .pathStyleAccessEnabled(true)
                    .build(),
            ).build()

    @Bean
    fun s3Presigner(): S3Presigner =
        S3Presigner
            .builder()
            .endpointOverride(URI.create(minioProperties.endpoint))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        minioProperties.accessKey,
                        minioProperties.secretKey,
                    ),
                ),
            ).region(Region.AP_NORTHEAST_2)
            .serviceConfiguration(
                S3Configuration
                    .builder()
                    .pathStyleAccessEnabled(true)
                    .build(),
            ).build()
}

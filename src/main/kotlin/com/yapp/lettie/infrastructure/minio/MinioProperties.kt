package com.yapp.lettie.infrastructure.minio

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("minio")
data class MinioProperties(
    var endpoint: String,
    var accessKey: String,
    var secretKey: String,
    var bucketName: String,
)

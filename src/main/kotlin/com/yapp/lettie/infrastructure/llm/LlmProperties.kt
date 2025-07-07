package com.yapp.lettie.infrastructure.llm

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("llm")
data class LlmProperties(
    val baseUrl: String,
    val key: String,
    val id: String,
    val serviceName: String,
)

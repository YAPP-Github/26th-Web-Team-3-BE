package com.yapp.demo.config

import com.yapp.demo.infrastructure.llm.LlmProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient

@Configuration
@EnableConfigurationProperties(LlmProperties::class)
class LlmConfiguration {
    @Bean
    fun llmWebClient(llmProperties: LlmProperties): WebClient {
        return WebClient.builder()
            .baseUrl(llmProperties.baseUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build()
    }
}

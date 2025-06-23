package com.yapp.demo.infrastructure.discord

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class DiscordNotifier(
    @Value("\${discord.webhook-url}") private val webhookUrl: String,
    private val webClientBuilder: WebClient.Builder,
) {
    fun notify(message: String) {
        webClientBuilder.build()
            .post()
            .uri(webhookUrl)
            .bodyValue(mapOf("content" to message))
            .retrieve()
            .toBodilessEntity()
            .subscribe(
                { /* 성공 */ },
                { error -> println("⚠️ Discord 알림 실패: ${error.message}") },
            )
    }
}

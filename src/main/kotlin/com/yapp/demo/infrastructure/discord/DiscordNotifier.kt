package com.yapp.demo.infrastructure.discord

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.time.Duration

@Component
class DiscordNotifier(
    @Value("\${discord.webhook-url}") private val webhookUrl: String,
    private val webClientBuilder: WebClient.Builder,
) {
    private val log = KotlinLogging.logger {}

    fun notify(message: String) {
        webClientBuilder.build()
            .post()
            .uri(webhookUrl)
            .bodyValue(mapOf("content" to message))
            .retrieve()
            .toBodilessEntity()
            .timeout(Duration.ofSeconds(10))
            .subscribe(
                { /* 성공 */ },
                { error -> log.error("Discord 알림 전송 실패", error) },
            )
    }
}

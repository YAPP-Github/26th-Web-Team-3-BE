package com.yapp.demo.common.error.reporter

import com.yapp.demo.common.error.analyzer.ErrorAnalyzer
import com.yapp.demo.common.error.analyzer.dto.AnalyzeErrorRequest
import com.yapp.demo.common.error.analyzer.dto.AnalyzeErrorResponse
import com.yapp.demo.infrastructure.discord.DiscordNotifier
import mu.KotlinLogging
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import java.time.Duration

// @Profile("dev")
@Component
class LlmErrorReporter(
    private val llmErrorAnalyzer: ErrorAnalyzer,
    private val discordNotifier: DiscordNotifier,
    private val redisTemplate: RedisTemplate<String, Boolean>,
) {
    private val log = KotlinLogging.logger {}

    @Async
    fun report(request: AnalyzeErrorRequest) {
        if (!request.notify) {
            log.info { "분석 요청 헤더가 없습니다." }
            return
        }

        val key = createCacheKey(request)
        val lockSuccess = redisTemplate.opsForValue().setIfAbsent(key, true, NOTIFY_DURATION) ?: false

        if (!lockSuccess) {
            log.info { "최근 10분 이내에 발송된 요청입니다." }
            return
        }

        try {
            val response = llmErrorAnalyzer.analyze(request)
//            if (!response.success) {
//                log.error { "처리에 실패했습니다. response=$response" }
//                return
//            }

            notify(request, response)
        } catch (e: Exception) {
            log.error(e) { "에러 분석 및 발송 중에 에러가 발생했습니다." }
            redisTemplate.delete(key)
        }
    }

    private fun createCacheKey(request: AnalyzeErrorRequest): String {
        return "${request.userId}:${request.path}-${request.httpMethod}-${request.exception.message}"
    }

    private fun notify(
        request: AnalyzeErrorRequest,
        response: AnalyzeErrorResponse,
    ) {
        val message =
            """
            ${response.json.action}
            ```
            action: ${response.json.action}
            request: ${request.httpMethod} ${request.path}
            reason: ${response.json.reason}
            solve: ${response.json.guide}
            ```
            """.trimIndent()

        discordNotifier.notify(message)
        redisTemplate.opsForValue().set(createCacheKey(request), true, NOTIFY_DURATION)
    }

    companion object {
        val NOTIFY_DURATION: Duration = Duration.ofMinutes(10)
    }
}

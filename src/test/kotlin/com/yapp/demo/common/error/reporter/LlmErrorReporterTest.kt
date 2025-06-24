package com.yapp.demo.common.error.reporter

import com.yapp.demo.common.error.analyzer.ErrorAnalyzer
import com.yapp.demo.common.error.analyzer.dto.AnalyzeErrorRequest
import com.yapp.demo.common.error.analyzer.dto.AnalyzeErrorResponse
import com.yapp.demo.infrastructure.discord.DiscordNotifier
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.redis.core.RedisTemplate
import kotlin.test.Test

@ExtendWith(MockKExtension::class)
class LlmErrorReporterTest {
    @MockK
    lateinit var llmErrorAnalyzer: ErrorAnalyzer

    @MockK
    lateinit var discordNotifier: DiscordNotifier

    @MockK
    lateinit var redisTemplate: RedisTemplate<String, Boolean>

    @InjectMockKs
    lateinit var reporter: LlmErrorReporter

    @Test
    fun `캐시되어 있지 않으면 예외를 분석하고 디스코드로 알림을 전송해야 한다`() {
        // given
        val request =
            AnalyzeErrorRequest(
                path = "/test",
                httpMethod = "GET",
                exception = RuntimeException("Test Exception"),
                userId = 123L,
                notify = true,
                logId = "log-1",
            )

        every { redisTemplate.opsForValue().setIfAbsent(any(), any(), any()) } returns true
        every { llmErrorAnalyzer.analyze(any()) } returns
            AnalyzeErrorResponse(
                success = true,
                json =
                    AnalyzeErrorResponse.Json(
                        action = "action",
                        reason = "reason",
                        guide = "guide",
                        inference = "inference",
                        apiSummary = "summary",
                    ),
            )
        every { discordNotifier.notify(any()) } returns Unit

        // when
        reporter.report(request)

        // then
        verify { llmErrorAnalyzer.analyze(request) }
        verify { discordNotifier.notify(match { it.contains("action") }) }
    }
}

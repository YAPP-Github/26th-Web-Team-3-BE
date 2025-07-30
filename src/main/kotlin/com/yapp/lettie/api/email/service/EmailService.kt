package com.yapp.lettie.api.email.service

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.springframework.beans.factory.DisposableBean
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class EmailService(
    private val mailSender: JavaMailSender,
) : DisposableBean {
    private val logger = KotlinLogging.logger {}
    private val ioScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun destroy() {
        ioScope.cancel()
    }

    fun sendTimeCapsuleOpenedEmail(
        recipients: List<String>,
        capsuleTitle: String,
        openDate: String,
        capsuleLink: String,
    ) {
        recipients.chunked(10).forEach { batch ->
            batch.forEach { recipient ->
                ioScope.launch {
                    delay(100)
                    try {
                        val message =
                            SimpleMailMessage().apply {
                                setTo(recipient)
                                setSubject("💌 타임캡슐이 열렸습니다! - $capsuleTitle")
                                setText(
                                    """
                                    안녕하세요!

                                    당신이 참여한 타임캡슐이 드디어 열렸습니다 🎉

                                    📬 캡슐 제목: $capsuleTitle
                                    📅 오픈 날짜: $openDate
                                    🔗 바로가기: $capsuleLink

                                    추억을 확인하러 지금 바로 방문해보세요!

                                    감사합니다.
                                    """.trimIndent(),
                                )
                            }

                        mailSender.send(message)
                        logger.info { "이메일 전송 성공 $recipient" }
                    } catch (e: Exception) {
                        logger.error(e) { "이메일 전송 실패 $recipient" }
                    }
                }
            }
        }
    }

    fun sendTestEmail(to: String) {
        ioScope.launch {
            try {
                val message =
                    SimpleMailMessage().apply {
                        setTo(to)
                        setSubject("테스트 메일입니다")
                        setText(
                            """
                            Lettie 이메일 전송 테스트입니다.

                            이 메일이 정상적으로 도착했다면 메일 설정이 올바르게 작동하고 있는 것입니다.

                            - Lettie 팀 드림
                            """.trimIndent(),
                        )
                    }

                mailSender.send(message)
                logger.info { "테스트 메일 전송 성공: $to" }
            } catch (e: Exception) {
                logger.error(e) { "테스트 메일 전송 실패: $to" }
            }
        }
    }
}

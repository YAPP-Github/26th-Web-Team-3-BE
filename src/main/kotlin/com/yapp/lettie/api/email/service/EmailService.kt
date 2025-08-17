package com.yapp.lettie.api.email.service

import jakarta.mail.internet.MimeMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.springframework.beans.factory.DisposableBean
import org.springframework.core.io.ClassPathResource
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

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
        recipientNames: List<String>,
        capsuleTitle: String,
        openDate: LocalDateTime,
        createdDate: LocalDateTime,
        capsuleLink: String,
    ) {
        val formattedDate = openDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))

        val duration = Duration.between(createdDate, openDate)
        val days = duration.toDays()
        val hours = duration.toHours()
        val minutes = duration.toMinutes()

        val elapsedText =
            when {
                days >= 1 -> "${days}일"
                hours >= 1 -> "${hours}시간"
                else -> "${minutes}분"
            }

        recipients.chunked(10).zip(recipientNames.chunked(10)).forEach { (batchRecipients, batchNames) ->
            batchRecipients.zip(batchNames).forEach { (recipient, name) ->
                ioScope.launch {
                    delay(100)
                    try {
                        val mimeMessage: MimeMessage = mailSender.createMimeMessage()
                        val helper = MimeMessageHelper(mimeMessage, true, "UTF-8")

                        helper.setTo(recipient)
                        helper.setSubject("[LETTIE] ${name}님, 방금 캡슐이 열렸어요! - $capsuleTitle ")

                        val bannerCid = "logo-${UUID.randomUUID()}"
                        val bannerRes = ClassPathResource("email/email-banner.jpg")
                        helper.addInline(bannerCid, bannerRes, "image/jpg")

                        val htmlContent =
                            """
                            <div style="font-family: 'Apple SD Gothic Neo', Arial, sans-serif; background-color: #f0f4f8; padding: 60px 20px;">
                                <div style="max-width: 500px; margin: 0 auto; background: #ffffff; border-radius: 14px; box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08); overflow: hidden;">

                                    <!-- 배너 이미지 -->
                                    <img src="cid:$bannerCid"
                                        alt="Lettie Banner"
                                        style="width: 100%; height: auto; display: block;" />

                                    <!-- 내용 -->
                                    <div style="padding: 20px 32px; text-align: center;">
                                        <h2 style="color: #37474F; font-size: 22px; margin-bottom: 16px;">기다리던 타임캡슐이 열렸어요!</h2>
                                        <p style="font-size: 14px; color: #607D8B; margin-bottom: 24px;">
                                            <strong>$elapsedText</strong> 만에 열린 캡슐을 열어보러 갈까요?
                                        </p>
                                        <div style="font-size: 24px; font-weight: bold; color: #3f51b5; margin: 30px 0 12px;">
                                            📬 $capsuleTitle
                                        </div>
                                        <p style="font-size: 14px; color: #888; margin-bottom: 32px;">
                                            오픈 시각: <strong>$formattedDate</strong>
                                        </p>
                                        <a href="$capsuleLink" target="_blank"
                                            style="display: inline-block; padding: 14px 28px; background: linear-gradient(135deg, #3f51b5, #5c6bc0); color: white; border-radius: 8px; text-decoration: none; font-weight: 600; font-size: 15px; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);">
                                            캡슐 열어보러 가기
                                        </a>
                                        <p style="font-size: 12px; color: #B0BEC5; margin-top: 40px;">
                                            그때의 기억을 추억하는 시간이 되기를 바라요. 💌<br/>
                                            - Lettie 팀 드림
                                        </p>
                                    </div>
                                </div>
                            </div>
                            """.trimIndent()

                        helper.setText(htmlContent, true)
                        mailSender.send(mimeMessage)
                        logger.info { "이메일 전송 성공: $recipient" }
                    } catch (e: Exception) {
                        logger.error(e) { "메일 전송 실패: $recipient" }
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

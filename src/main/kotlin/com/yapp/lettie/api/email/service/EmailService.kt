package com.yapp.lettie.api.email.service

import com.yapp.lettie.domain.timecapsule.dto.RecipientRow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.springframework.beans.factory.DisposableBean
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
        recipients: List<RecipientRow>,
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

        recipients.chunked(10).forEach { batch ->
            batch.forEach { recipient ->
                ioScope.launch {
                    delay(100)
                    try {
                        val mime = mailSender.createMimeMessage()
                        val helper =
                            MimeMessageHelper(
                                mime,
                                "UTF-8",
                            )

                        helper.setTo(recipient.email)
                        helper.setSubject("[LETTIE] ${recipient.name}님, 방금 캡슐이 열렸어요! - $capsuleTitle ")

                        val htmlContent =
                            """
                            <div style="font-family: 'Apple SD Gothic Neo', Arial, sans-serif; background-color: #f0f4f8; padding: 60px 20px;">
                                <div style="max-width: 500px; margin: 0 auto; background: #ffffff; border-radius: 14px; box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08); overflow: hidden;">

                                    <!-- 배너 이미지 -->
                                    <img src="https://s3.lettie.me/object/EMAIL/email-banner.jpg"
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
                        mailSender.send(mime)
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
                val mime = mailSender.createMimeMessage()
                val helper =
                    MimeMessageHelper(
                        mime,
                        "UTF-8",
                    )
                helper.setTo(to)
                helper.setSubject("[LETTIE] Test User님, 방금 캡슐이 열렸어요! - test 캡슐 ")

                val htmlContent =
                    """
                    <div style="font-family: 'Apple SD Gothic Neo', Arial, sans-serif; background-color: #f0f4f8; padding: 60px 20px;">
                        <div style="max-width: 500px; margin: 0 auto; background: #ffffff; border-radius: 14px; box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08); overflow: hidden;">

                            <!-- 배너 이미지 -->
                            <img src="https://s3.lettie.me/object/EMAIL/email-banner.jpg"
                            alt="Lettie Banner"
                            style="width: 100%; height: auto; display: block;" />

                            <!-- 내용 -->
                            <div style="padding: 20px 32px; text-align: center;">
                                <h2 style="color: #37474F; font-size: 22px; margin-bottom: 16px;">기다리던 타임캡슐이 열렸어요!</h2>
                                <p style="font-size: 14px; color: #607D8B; margin-bottom: 24px;">
                                    <strong>1시간</strong> 만에 열린 캡슐을 열어보러 갈까요?
                                </p>
                                <div style="font-size: 24px; font-weight: bold; color: #3f51b5; margin: 30px 0 12px;">
                                    📬 test
                                </div>
                                <p style="font-size: 14px; color: #888; margin-bottom: 32px;">
                                    오픈 시각: <strong>12:00</strong>
                                </p>
                                <a href="https://lettie.me" target="_blank"
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

                val plainText =
                    """
                    Test 님, 기다리던 타임캡슐이 열렸어요!

                    캡슐: test
                    오픈 시각: 12:00

                    바로 열어보기: https://lettie.com

                    - Lettie 팀 드림
                    """.trimIndent()

                helper.setText(htmlContent, true)
                mailSender.send(mime)
                logger.info { "테스트 메일 전송 성공: $to" }
            } catch (e: Exception) {
                logger.error(e) { "테스트 메일 전송 실패: $to" }
            }
        }
    }
}

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
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
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
        recipients: List<String>,
        capsuleTitle: String,
        openDate: LocalDateTime,
        capsuleLink: String,
    ) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val formattedDate = openDate.format(formatter)

        recipients.chunked(10).forEach { batch ->
            batch.forEach { recipient ->
                ioScope.launch {
                    delay(100)
                    try {
                        val mimeMessage: MimeMessage = mailSender.createMimeMessage()
                        val helper = MimeMessageHelper(mimeMessage, false, "UTF-8")

                        helper.setTo(recipient)
                        helper.setSubject("💌 당신의 타임캡슐이 열렸습니다! - $capsuleTitle")

                        val htmlContent =
                            """
                            <html>
                            <head>
                              <style>
                                body {
                                  font-family: 'Arial', sans-serif;
                                  background-color: #f9f9f9;
                                  padding: 20px;
                                  color: #333;
                                }
                                .card {
                                  background: #fff;
                                  padding: 20px;
                                  border-radius: 10px;
                                  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
                                  max-width: 600px;
                                  margin: auto;
                                }
                                .title {
                                  font-size: 20px;
                                  font-weight: bold;
                                  margin-bottom: 16px;
                                  color: #3f51b5;
                                }
                                .label {
                                  font-weight: bold;
                                  margin-top: 10px;
                                }
                                .button {
                                  margin-top: 20px;
                                  display: inline-block;
                                  padding: 10px 20px;
                                  background-color: #3f51b5;
                                  color: white;
                                  text-decoration: none;
                                  border-radius: 6px;
                                  font-weight: bold;
                                }
                                .footer {
                                  margin-top: 30px;
                                  font-size: 12px;
                                  color: #777;
                                }
                              </style>
                            </head>
                            <body>
                              <div class="card">
                                <div class="title">🎉 타임캡슐이 열렸어요!</div>
                                <div><span class="label">📬 캡슐 제목:</span> $capsuleTitle</div>
                                <div><span class="label">📅 오픈 시간:</span> $formattedDate</div>
                                <a class="button" href="$capsuleLink" target="_blank">캡슐 바로 확인하기</a>
                                <div class="footer">
                                  함께한 추억을 되돌아보는 따뜻한 시간 되세요 💌<br/>
                                  - Lettie 팀 드림
                                </div>
                              </div>
                            </body>
                            </html>
                            """.trimIndent()

                        helper.setText(htmlContent, true) // true: HTML

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

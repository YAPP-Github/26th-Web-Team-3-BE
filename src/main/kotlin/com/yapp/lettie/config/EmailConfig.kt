package com.yapp.lettie.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import java.util.Properties

@Configuration
class EmailConfig(
    @Value("\${mail.host}") private val host: String,
    @Value("\${mail.port}") private val port: Int,
    @Value("\${mail.username}") private val username: String,
    @Value("\${mail.password}") private val password: String,
    @Value("\${mail.properties.mail.smtp.auth}") private val auth: Boolean,
    @Value("\${mail.properties.mail.smtp.timeout}") private val timeout: Int,
    @Value("\${mail.properties.mail.smtp.connection-timeout}") private val connectionTimeout: Int,
    @Value("\${mail.properties.mail.smtp.write-timeout}") private val writeTimeout: Int,
    @Value("\${mail.properties.mail.smtp.starttls.enable}") private val starttlsEnable: Boolean,
) {
    @Bean
    fun javaMailSender(): JavaMailSender {
        val mailSender = JavaMailSenderImpl()
        mailSender.host = host
        mailSender.port = port
        mailSender.username = username
        mailSender.password = password
        mailSender.defaultEncoding = "UTF-8"

        val props = Properties()
        props["mail.smtp.auth"] = auth
        props["mail.smtp.starttls.enable"] = starttlsEnable
        props["mail.smtp.timeout"] = timeout
        props["mail.smtp.connectiontimeout"] = connectionTimeout
        props["mail.smtp.writetimeout"] = writeTimeout

        mailSender.javaMailProperties = props

        return mailSender
    }
}

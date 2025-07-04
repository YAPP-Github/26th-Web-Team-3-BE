package com.yapp.demo.common.controller

import mu.KotlinLogging
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@Profile("!prod")
@RestController
class ExceptionTestController {
    private val log = KotlinLogging.logger {}

    @GetMapping("/test/exception")
    fun throwException(): String {
        throw RuntimeException("테스트용 예외가 발생했습니다!")
    }

    @GetMapping("/log-test")
    fun logTest(): String {
        log.info { "테스트 로그: 로그 파일 생성 여부 확인용!" }
        return "로그 테스트 OK"
    }
}

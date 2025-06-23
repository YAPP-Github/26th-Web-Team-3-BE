package com.yapp.demo

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ExceptionTestController {
    @GetMapping("/test/exception")
    fun throwException(): String {
        throw RuntimeException("테스트용 예외가 발생했습니다!")
    }
}

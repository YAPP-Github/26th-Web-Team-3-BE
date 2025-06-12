package com.yapp.demo.common.controller

import com.yapp.demo.common.exception.BadRequestException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/test")
class TestController {
    @GetMapping("/error")
    fun throwBadRequest(): String {
        throw BadRequestException("demo.test.not-found")
    }
}

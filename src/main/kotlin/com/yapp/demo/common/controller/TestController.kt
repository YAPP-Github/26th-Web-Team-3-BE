package com.yapp.demo.common.controller

import com.yapp.demo.common.error.ErrorMessages
import com.yapp.demo.common.exception.ApiErrorException
import com.yapp.demo.common.exception.BadRequestException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/test")
class TestController {
    @GetMapping("/error-1")
    fun throwApiErrorException(): String {
        throw ApiErrorException(ErrorMessages.INVALID_INPUT_VALUE)
    }

    @GetMapping("/error-2")
    fun throwBadRequest(): String {
        throw BadRequestException()
    }
}

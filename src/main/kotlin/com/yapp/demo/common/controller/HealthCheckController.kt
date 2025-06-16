package com.yapp.demo.common.controller

import com.yapp.demo.common.dto.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/health")
class HealthCheckController {

    @GetMapping
    fun healthCheck(): ApiResponse<String> {
        return ApiResponse.success("OK")
    }
}

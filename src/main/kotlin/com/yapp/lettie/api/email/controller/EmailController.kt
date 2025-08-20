package com.yapp.lettie.api.email.controller

import com.yapp.lettie.api.email.service.EmailService
import com.yapp.lettie.common.dto.ApiResponse
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/mail")
@Profile("!(dev | prod)")
class EmailController(
    private val emailService: EmailService,
) {
    @PostMapping("/test")
    fun testEmail(
        @RequestParam email: String,
    ): ResponseEntity<ApiResponse<Boolean>> {
        emailService.sendTestEmail(email)
        return ResponseEntity.ok(ApiResponse.success(true))
    }
}

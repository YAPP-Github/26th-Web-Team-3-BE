package com.yapp.demo.api.auth.controller

import com.yapp.demo.api.auth.controller.request.AuthorizationRequest
import com.yapp.demo.api.auth.controller.response.JwtTokenResponse
import com.yapp.demo.api.auth.controller.response.OAuthUrlResponse
import com.yapp.demo.api.auth.service.AuthService
import com.yapp.demo.common.dto.ApiResponse
import com.yapp.demo.config.AuthConfig
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthApiController(
    val authConfig: AuthConfig,
    val authService: AuthService,
) : AuthEndPoint {
    @GetMapping("/oauth/kakao")
    override fun googleAuth(): ResponseEntity<ApiResponse<OAuthUrlResponse>> =
        ResponseEntity.ok().body(
            ApiResponse.success(
                OAuthUrlResponse(authConfig.oauthUrl()),
            ),
        )

    @PostMapping("/code/kakao")
    override fun login(request: AuthorizationRequest): ResponseEntity<ApiResponse<JwtTokenResponse>> =
        ResponseEntity.ok().body(
            ApiResponse.success(
                JwtTokenResponse.of(authService.kakaoLogin(request.authorizationCode)),
            ),
        )
}

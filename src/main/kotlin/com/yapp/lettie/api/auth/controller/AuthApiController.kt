package com.yapp.lettie.api.auth.controller

import com.yapp.lettie.api.auth.controller.request.AuthorizationRequest
import com.yapp.lettie.api.auth.controller.response.JwtTokenResponse
import com.yapp.lettie.api.auth.controller.response.OAuthUrlResponse
import com.yapp.lettie.api.auth.service.AuthService
import com.yapp.lettie.common.dto.ApiResponse
import com.yapp.lettie.config.AuthConfig
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
) : AuthSwagger {
    @GetMapping("/oauth/kakao")
    override fun kakaoAuth(): ResponseEntity<ApiResponse<OAuthUrlResponse>> =
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

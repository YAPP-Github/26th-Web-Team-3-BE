package com.yapp.lettie.api.auth.controller

import com.yapp.lettie.api.auth.controller.request.AuthorizationRequest
import com.yapp.lettie.api.auth.controller.response.JwtTokenResponse
import com.yapp.lettie.api.auth.controller.response.OAuthUrlResponse
import com.yapp.lettie.api.auth.service.AuthService
import com.yapp.lettie.common.dto.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthApiController(
    val authService: AuthService,
) : AuthSwagger {
    @Deprecated("Use google or naver instead", ReplaceWith("googleAuth(), naverAuth()"))
    @GetMapping("/oauth/kakao")
    override fun kakaoAuth(): ResponseEntity<ApiResponse<OAuthUrlResponse>> =
        ResponseEntity.ok().body(
            ApiResponse.success(
                OAuthUrlResponse(authService.getKakaoLoginUrl()),
            ),
        )

    @Deprecated("Use google or naver instead", ReplaceWith("googleLogin(), naverLogin()"))
    @PostMapping("/code/kakao")
    override fun kakaoLogin(request: AuthorizationRequest): ResponseEntity<ApiResponse<JwtTokenResponse>> =
        ResponseEntity.ok().body(
            ApiResponse.success(
                JwtTokenResponse.of(authService.kakaoLogin(request.authorizationCode)),
            ),
        )

    @GetMapping("/oauth/google")
    override fun googleAuth(): ResponseEntity<ApiResponse<OAuthUrlResponse>> =
        ResponseEntity.ok().body(
            ApiResponse.success(
                OAuthUrlResponse(authService.getGoogleLoginUrl()),
            ),
        )

    @PostMapping("/code/google")
    override fun googleLogin(request: AuthorizationRequest): ResponseEntity<ApiResponse<JwtTokenResponse>> =
        ResponseEntity.ok().body(
            ApiResponse.success(
                JwtTokenResponse.of(authService.googleLogin(request.authorizationCode)),
            ),
        )

    @GetMapping("/oauth/naver")
    override fun naverAuth(): ResponseEntity<ApiResponse<OAuthUrlResponse>> =
        ResponseEntity.ok().body(
            ApiResponse.success(
                OAuthUrlResponse(authService.getNaverLoginUrl()),
            ),
        )

    @PostMapping("/code/naver")
    override fun naverLogin(request: AuthorizationRequest): ResponseEntity<ApiResponse<JwtTokenResponse>> =
        ResponseEntity.ok().body(
            ApiResponse.success(
                JwtTokenResponse.of(authService.naverLogin(request.authorizationCode)),
            ),
        )
}

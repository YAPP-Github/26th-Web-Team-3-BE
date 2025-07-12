package com.yapp.lettie.api.auth.controller

import com.yapp.lettie.api.auth.controller.request.AuthorizationRequest
import com.yapp.lettie.api.auth.controller.response.JwtTokenResponse
import com.yapp.lettie.api.auth.controller.response.OAuthUrlResponse
import com.yapp.lettie.common.dto.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody

@Tag(name = "auth", description = "인증 API")
interface AuthSwagger {
    @Operation(summary = "카카오 oauth url 조회", description = "카카오 oauth url을 조회합니다.")
    fun kakaoAuth(): ResponseEntity<ApiResponse<OAuthUrlResponse>>

    @Operation(summary = "카카오 로그인 요청", description = "카카오 로그인을 요청합니다.")
    fun kakaoLogin(
        @RequestBody request: AuthorizationRequest,
    ): ResponseEntity<ApiResponse<JwtTokenResponse>>

    @Operation(summary = "구글 oauth url 조회", description = "구글 oauth url을 조회합니다.")
    fun googleAuth(): ResponseEntity<ApiResponse<OAuthUrlResponse>>

    @Operation(summary = "구글 로그인 요청", description = "구글 로그인을 요청합니다.")
    fun googleLogin(
        @RequestBody request: AuthorizationRequest,
    ): ResponseEntity<ApiResponse<JwtTokenResponse>>

    @Operation(summary = "네이버 oauth url 조회", description = "네이버 oauth url을 조회합니다.")
    fun naverAuth(): ResponseEntity<ApiResponse<OAuthUrlResponse>>

    @Operation(summary = "네이버 로그인 요청", description = "네이버 로그인을 요청합니다.")
    fun naverLogin(
        @RequestBody request: AuthorizationRequest,
    ): ResponseEntity<ApiResponse<JwtTokenResponse>>
}

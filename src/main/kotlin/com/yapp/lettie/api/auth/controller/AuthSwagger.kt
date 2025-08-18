package com.yapp.lettie.api.auth.controller

import com.yapp.lettie.api.auth.controller.request.AuthorizationRequest
import com.yapp.lettie.api.auth.controller.response.JwtTokenResponse
import com.yapp.lettie.api.auth.controller.response.OAuthUrlResponse
import com.yapp.lettie.common.dto.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody

@Tag(name = "auth", description = "인증 API")
interface AuthSwagger {
    @Operation(summary = "카카오 oauth url 조회", description = "카카오 oauth url을 조회합니다.")
    fun kakaoAuth(
        @Schema(description = "카카오 로그인 후 리다이렉트될 URL (URL 인코딩 필요)")
        redirectUrl: String,
    ): ResponseEntity<ApiResponse<OAuthUrlResponse>>

    @Operation(summary = "카카오 로그인 요청", description = "카카오 로그인을 요청합니다.")
    fun kakaoLogin(
        @RequestBody request: AuthorizationRequest,
    ): ResponseEntity<ApiResponse<JwtTokenResponse>>

    @Operation(summary = "구글 oauth url 조회", description = "구글 oauth url을 조회합니다.")
    fun googleAuth(
        @Schema(description = "구글 로그인 후 리다이렉트될 URL (URL 인코딩 필요)")
        redirectUrl: String,
        @Schema(description = "구글 로그인 후 상태값 (선택사항)", example = "state")
        state: String? = null,
    ): ResponseEntity<ApiResponse<OAuthUrlResponse>>

    @Operation(
        summary = "구글 로그인 요청",
        description = "구글 로그인을 요청합니다. 성공 시 JWT 토큰이 HTTP Only 쿠키로 설정됩니다.",
    )
    fun googleLogin(
        @RequestBody request: AuthorizationRequest,
        @Parameter(hidden = true) response: HttpServletResponse,
    ): ResponseEntity<ApiResponse<JwtTokenResponse>>

    @Operation(summary = "네이버 oauth url 조회", description = "네이버 oauth url을 조회합니다.")
    fun naverAuth(
        @Schema(description = "네이버 로그인 후 리다이렉트될 URL (URL 인코딩 필요)")
        redirectUrl: String,
        @Schema(description = "네이버 로그인 후 상태값 (선택사항)", example = "state")
        state: String? = null,
    ): ResponseEntity<ApiResponse<OAuthUrlResponse>>

    @Operation(
        summary = "네이버 로그인 요청",
        description = "네이버 로그인을 요청합니다. 성공 시 JWT 토큰이 HTTP Only 쿠키로 설정됩니다.",
    )
    fun naverLogin(
        @RequestBody request: AuthorizationRequest,
        @Parameter(hidden = true) response: HttpServletResponse,
    ): ResponseEntity<ApiResponse<JwtTokenResponse>>

    @Operation(
        summary = "로그아웃",
        description = "로그아웃을 수행합니다. HTTP Only 쿠키의 JWT 토큰을 삭제합니다.",
    )
    fun logout(
        @Parameter(hidden = true) response: HttpServletResponse,
    ): ResponseEntity<ApiResponse<Boolean>>
}

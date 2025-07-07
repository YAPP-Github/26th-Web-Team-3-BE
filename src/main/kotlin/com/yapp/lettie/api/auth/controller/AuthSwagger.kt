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
    fun login(
        @RequestBody request: AuthorizationRequest,
    ): ResponseEntity<ApiResponse<JwtTokenResponse>>
}

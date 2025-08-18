package com.yapp.lettie.api.auth.controller

import com.yapp.lettie.api.auth.component.CookieComponent
import com.yapp.lettie.api.auth.controller.request.AuthorizationRequest
import com.yapp.lettie.api.auth.controller.response.JwtTokenResponse
import com.yapp.lettie.api.auth.controller.response.OAuthUrlResponse
import com.yapp.lettie.api.auth.service.AuthService
import com.yapp.lettie.common.dto.ApiResponse
import com.yapp.lettie.common.error.ErrorMessages
import com.yapp.lettie.common.exception.ApiErrorException
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthApiController(
    private val authService: AuthService,
    private val cookieComponent: CookieComponent,
) : AuthSwagger {
    @Deprecated("Use google or naver instead", ReplaceWith("googleAuth(), naverAuth()"))
    @GetMapping("/oauth/kakao")
    override fun kakaoAuth(redirectUrl: String): ResponseEntity<ApiResponse<OAuthUrlResponse>> =
        ResponseEntity.ok().body(
            ApiResponse.success(
                OAuthUrlResponse(authService.getKakaoLoginUrl(redirectUrl)),
            ),
        )

    @Deprecated("Use google or naver instead", ReplaceWith("googleLogin(), naverLogin()"))
    @PostMapping("/code/kakao")
    override fun kakaoLogin(request: AuthorizationRequest): ResponseEntity<ApiResponse<JwtTokenResponse>> {
        if (request.redirectUrl == null) {
            throw ApiErrorException(ErrorMessages.REDIRECT_URL_REQUIRED)
        }

        return ResponseEntity.ok().body(
            ApiResponse.success(
                JwtTokenResponse.of(authService.kakaoLogin(request.authorizationCode, request.redirectUrl)),
            ),
        )
    }

    @GetMapping("/oauth/google")
    override fun googleAuth(
        redirectUrl: String,
        state: String?,
    ): ResponseEntity<ApiResponse<OAuthUrlResponse>> =
        ResponseEntity.ok().body(
            ApiResponse.success(
                OAuthUrlResponse(authService.getGoogleLoginUrl(redirectUrl, state)),
            ),
        )

    @PostMapping("/code/google")
    override fun googleLogin(
        @RequestBody request: AuthorizationRequest,
        response: HttpServletResponse,
    ): ResponseEntity<ApiResponse<JwtTokenResponse>> {
        if (request.redirectUrl == null) {
            throw ApiErrorException(ErrorMessages.REDIRECT_URL_REQUIRED)
        }

        val jwtTokenDto = authService.googleLogin(request.authorizationCode, request.redirectUrl, request.state)
        cookieComponent.setAccessTokenCookie(jwtTokenDto, response)

        return ResponseEntity.ok().body(
            ApiResponse.success(
                JwtTokenResponse.of(jwtTokenDto),
            ),
        )
    }

    @GetMapping("/oauth/naver")
    override fun naverAuth(
        redirectUrl: String,
        state: String?,
    ): ResponseEntity<ApiResponse<OAuthUrlResponse>> =
        ResponseEntity.ok().body(
            ApiResponse.success(
                OAuthUrlResponse(authService.getNaverLoginUrl(redirectUrl, state)),
            ),
        )

    @PostMapping("/code/naver")
    override fun naverLogin(
        @RequestBody request: AuthorizationRequest,
        response: HttpServletResponse,
    ): ResponseEntity<ApiResponse<JwtTokenResponse>> {
        val jwtTokenDto = authService.naverLogin(request.authorizationCode, request.state)
        cookieComponent.setAccessTokenCookie(jwtTokenDto, response)

        return ResponseEntity.ok().body(
            ApiResponse.success(
                JwtTokenResponse.of(jwtTokenDto),
            ),
        )
    }

    @PostMapping("/logout")
    override fun logout(response: HttpServletResponse): ResponseEntity<ApiResponse<Boolean>> {
        cookieComponent.clearAccessTokenCookie(response)

        return ResponseEntity.ok().body(
            ApiResponse.success(true),
        )
    }
}

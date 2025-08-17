package com.yapp.lettie.api.auth.component

import com.yapp.lettie.api.auth.service.dto.JwtTokenDto
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class CookieComponent(
    @Value("\${jwt.expiration}") private val accessTokenExpireLength: Long,
    @Value("\${cookie.secure}") private val cookieSecure: Boolean,
    @Value("\${cookie.same-site}") private val sameSite: String,
    @Value("\${cookie.domain}") private val domain: String,
) {
    companion object {
        const val ACCESS_TOKEN_COOKIE_NAME = "accessToken"
    }

    /**
     * JWT 토큰을 HTTP Only 쿠키로 설정합니다.
     */
    fun setAccessTokenCookie(
        jwtTokenDto: JwtTokenDto,
        response: HttpServletResponse,
    ) {
        val cookie =
            ResponseCookie
                .from(ACCESS_TOKEN_COOKIE_NAME, jwtTokenDto.token)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(Duration.ofMillis(accessTokenExpireLength))
                .sameSite(sameSite)
                .domain(domain)
                .build()
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())
    }

    /**
     * 액세스 토큰 쿠키를 삭제합니다.
     */
    fun clearAccessTokenCookie(response: HttpServletResponse) {
        val cookie =
            ResponseCookie
                .from(ACCESS_TOKEN_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(Duration.ZERO)
                .sameSite(sameSite)
                .domain(domain)
                .build()
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())
    }
}

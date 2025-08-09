package com.yapp.lettie.api.auth.component

import com.yapp.lettie.api.auth.service.dto.JwtTokenDto
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class CookieComponent(
    @Value("\${jwt.expiration}") private val accessTokenExpireLength: Long,
    @Value("\${cookie.secure:false}") private val cookieSecure: Boolean,
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
            Cookie(ACCESS_TOKEN_COOKIE_NAME, jwtTokenDto.token).apply {
                isHttpOnly = true // HTTP Only로 변경 (보안상 권장)
                secure = cookieSecure // 프로퍼티로 설정
                path = "/"
                maxAge = (accessTokenExpireLength / 1000).toInt() // 밀리초를 초로 변환
            }
        response.addCookie(cookie)
    }

    /**
     * 액세스 토큰 쿠키를 삭제합니다.
     */
    fun clearAccessTokenCookie(response: HttpServletResponse) {
        val cookie =
            Cookie(ACCESS_TOKEN_COOKIE_NAME, "").apply {
                isHttpOnly = true
                secure = cookieSecure // 프로퍼티로 설정
                path = "/"
                maxAge = 0 // 쿠키 삭제
            }
        response.addCookie(cookie)
    }
}

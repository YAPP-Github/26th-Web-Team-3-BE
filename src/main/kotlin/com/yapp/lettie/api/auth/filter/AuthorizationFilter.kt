package com.yapp.lettie.api.auth.filter

import com.fasterxml.jackson.databind.ObjectMapper
import com.yapp.lettie.api.ApiPath
import com.yapp.lettie.api.auth.component.CookieComponent
import com.yapp.lettie.api.auth.component.JwtComponent
import com.yapp.lettie.common.dto.ApiResponse
import com.yapp.lettie.common.dto.UserInfoPayload
import com.yapp.lettie.common.error.ErrorMessages
import com.yapp.lettie.common.exception.ApiErrorException
import com.yapp.lettie.domain.auth.AuthType
import com.yapp.lettie.domain.user.UserRole
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class AuthorizationFilter(
    private val jwtComponent: JwtComponent,
    private val objectMapper: ObjectMapper,
) : OncePerRequestFilter() {
    companion object {
        const val CURRENT_USER_KEY = "currentUser"
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        try {
            val requestURI = request.requestURI
            val httpMethod = request.method

            // API 권한 정보 조회
            val authType = ApiPath.getAuthType(requestURI, httpMethod)

            when (authType) {
                AuthType.REQUIRED -> {
                    handleRequiredAuth(request)
                }

                AuthType.OPTIONAL -> {
                    handleOptionalAuth(request)
                }

                AuthType.NONE -> {
                    // 인증이 필요 없는 엔드포인트는 추가 처리 없이 통과
                }
            }

            filterChain.doFilter(request, response)
        } catch (e: ApiErrorException) {
            handleException(response, e)
        } catch (e: Exception) {
            handleException(response, ApiErrorException(ErrorMessages.INTERNAL_SERVER_ERROR))
        }
    }

    private fun handleRequiredAuth(request: HttpServletRequest) {
        val token = getTokenFromRequest(request) ?: throw ApiErrorException(ErrorMessages.UNAUTHORIZED)
        val claims = jwtComponent.verify(token)

        val userInfo =
            UserInfoPayload(
                id = claims.id.toLong(),
                roles = claims.roles.toList(),
            )

        request.setAttribute(CURRENT_USER_KEY, userInfo)
    }

    private fun handleOptionalAuth(request: HttpServletRequest) {
        val token = getTokenFromRequest(request)

        if (token != null) {
            try {
                val claims = jwtComponent.verify(token)
                val userInfo =
                    UserInfoPayload(
                        id = claims.id.toLong(),
                        roles = claims.roles.toList(),
                    )
                request.setAttribute(CURRENT_USER_KEY, userInfo)
            } catch (e: Exception) {
                // 토큰이 유효하지 않으면 기본 사용자 설정
                setDefaultUser(request)
            }
        } else {
            // 토큰이 없으면 기본 사용자 설정
            setDefaultUser(request)
        }
    }

    private fun setDefaultUser(request: HttpServletRequest) {
        val defaultUser =
            UserInfoPayload(
                id = -1L,
                roles = listOf(UserRole.GUEST.name),
            )
        request.setAttribute(CURRENT_USER_KEY, defaultUser)
    }

    private fun getTokenFromRequest(request: HttpServletRequest): String? {
        request.cookies?.let { cookies ->
            cookies.find { it.name == CookieComponent.ACCESS_TOKEN_COOKIE_NAME }?.let { cookie ->
                return cookie.value
            }
        }

        return null
    }

    private fun handleException(
        response: HttpServletResponse,
        exception: ApiErrorException,
    ) {
        response.status = exception.error.status.code
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = "UTF-8"

        val errorResponse = ApiResponse.error<Any>(exception.error)

        response.writer.write(objectMapper.writeValueAsString(errorResponse))
        response.writer.flush()
    }
}

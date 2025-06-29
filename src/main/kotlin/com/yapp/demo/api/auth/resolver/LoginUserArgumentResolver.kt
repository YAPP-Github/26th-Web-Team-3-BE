package com.yapp.demo.api.auth.resolver

import com.yapp.demo.api.auth.annotation.LoginUser
import com.yapp.demo.api.auth.component.JwtComponent
import com.yapp.demo.common.dto.UserInfoDto
import com.yapp.demo.common.error.ErrorMessages
import com.yapp.demo.common.exception.ApiErrorException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class LoginUserArgumentResolver(
    private val jwtComponent: JwtComponent,
) : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean =
        parameter.getParameterAnnotation(LoginUser::class.java) != null

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): Any? {
        val request = webRequest.getNativeRequest(HttpServletRequest::class.java)
        val token = getTokenFromRequest(request) ?: throw ApiErrorException(ErrorMessages.UNAUTHORIZED)

        val claims =
            token.let { jwtComponent.verify(it) }

        return UserInfoDto(
            id = claims.id.toLong(),
            roles = claims.roles.toList(),
        )
    }

    private fun getTokenFromRequest(request: HttpServletRequest?): String? {
        val bearerToken = request?.getHeader("Authorization")
        return if (!bearerToken.isNullOrBlank() && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else {
            null
        }
    }
}

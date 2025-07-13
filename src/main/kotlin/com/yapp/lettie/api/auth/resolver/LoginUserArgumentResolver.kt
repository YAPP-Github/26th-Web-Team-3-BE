package com.yapp.lettie.api.auth.resolver

import com.yapp.lettie.api.auth.annotation.LoginUser
import com.yapp.lettie.api.auth.filter.AuthorizationFilter
import com.yapp.lettie.common.dto.UserInfoDto
import com.yapp.lettie.common.error.ErrorMessages
import com.yapp.lettie.common.exception.ApiErrorException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class LoginUserArgumentResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean =
        parameter.getParameterAnnotation(LoginUser::class.java) != null

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): Any? {
        val request = webRequest.getNativeRequest(HttpServletRequest::class.java)

        return request?.getAttribute(AuthorizationFilter.CURRENT_USER_KEY) as? UserInfoDto
            ?: throw ApiErrorException(ErrorMessages.INTERNAL_SERVER_ERROR)
    }
}

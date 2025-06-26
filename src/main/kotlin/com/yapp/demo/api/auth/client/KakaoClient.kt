package com.yapp.demo.api.auth.client

import com.yapp.demo.api.auth.client.dto.KakaoTokenResponse
import com.yapp.demo.api.auth.client.dto.KakaoUserInfoResponse
import com.yapp.demo.api.auth.service.dto.KakaoUserInfoDto
import com.yapp.demo.common.error.ErrorMessages
import com.yapp.demo.common.exception.ApiErrorException
import com.yapp.demo.config.AuthConfig
import com.yapp.demo.util.RestClientUtil
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap

@Component
class KakaoClient(
    private val authConfig: AuthConfig,
    private val restClientUtil: RestClientUtil,
) {
    fun kakaoLogin(authorizationCode: String): KakaoUserInfoDto {
        val tokenRequestParams =
            LinkedMultiValueMap<String, String>().apply {
                add("grant_type", authConfig.grantType)
                add("client_id", authConfig.clientId)
                add("redirect_uri", authConfig.redirectUri)
                add("code", authorizationCode)
            }

        val tokenResponse =
            restClientUtil.post(
                url = authConfig.tokenUrl,
                body = tokenRequestParams,
                responseType = KakaoTokenResponse::class.java,
            ) ?: throw ApiErrorException(ErrorMessages.INVALID_INPUT_VALUE)

        val headers =
            mapOf(
                "Authorization" to tokenResponse.getBearerToken(),
            )

        val userInfoResponse =
            restClientUtil.get(
                url = authConfig.userInfoUrl,
                headers = headers,
                responseType = KakaoUserInfoResponse::class.java,
            ) ?: throw ApiErrorException(ErrorMessages.INTERNAL_SERVER_ERROR)

        return userInfoResponse.toDto()
    }
}

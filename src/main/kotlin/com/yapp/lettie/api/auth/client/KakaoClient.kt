package com.yapp.lettie.api.auth.client

import com.yapp.lettie.api.auth.client.dto.KakaoTokenResponse
import com.yapp.lettie.api.auth.client.dto.KakaoUserInfoResponse
import com.yapp.lettie.api.auth.service.dto.KakaoUserInfoDto
import com.yapp.lettie.common.error.ErrorMessages
import com.yapp.lettie.common.exception.ApiErrorException
import com.yapp.lettie.config.AuthConfig
import com.yapp.lettie.util.RestClientUtil
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

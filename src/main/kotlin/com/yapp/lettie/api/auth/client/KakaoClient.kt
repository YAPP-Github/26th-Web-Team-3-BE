package com.yapp.lettie.api.auth.client

import com.yapp.lettie.api.auth.client.dto.KakaoTokenResponse
import com.yapp.lettie.api.auth.client.dto.KakaoUserInfoResponse
import com.yapp.lettie.api.auth.service.dto.AuthUserInfoDto
import com.yapp.lettie.common.error.ErrorMessages
import com.yapp.lettie.common.exception.ApiErrorException
import com.yapp.lettie.config.AuthKakaoConfig
import com.yapp.lettie.util.RestClientUtil
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap

@Component
class KakaoClient(
    private val authKakaoConfig: AuthKakaoConfig,
    private val restClientUtil: RestClientUtil,
) {
    fun login(
        authorizationCode: String,
        redirectUrl: String,
    ): AuthUserInfoDto {
        val tokenRequestParams =
            LinkedMultiValueMap<String, String>().apply {
                add("grant_type", authKakaoConfig.grantType)
                add("client_id", authKakaoConfig.clientId)
                add("redirect_uri", redirectUrl)
                add("code", authorizationCode)
            }

        val tokenResponse =
            restClientUtil.post(
                url = authKakaoConfig.tokenUrl,
                body = tokenRequestParams,
                responseType = KakaoTokenResponse::class.java,
            ) ?: throw ApiErrorException(ErrorMessages.INVALID_INPUT_VALUE)

        val headers =
            mapOf(
                "Authorization" to tokenResponse.getBearerToken(),
            )

        val userInfoResponse =
            restClientUtil.get(
                url = authKakaoConfig.userInfoUrl,
                headers = headers,
                responseType = KakaoUserInfoResponse::class.java,
            ) ?: throw ApiErrorException(ErrorMessages.INTERNAL_SERVER_ERROR)

        return userInfoResponse.toDto()
    }
}

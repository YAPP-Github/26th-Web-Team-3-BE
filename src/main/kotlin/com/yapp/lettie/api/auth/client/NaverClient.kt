package com.yapp.lettie.api.auth.client

import com.yapp.lettie.api.auth.client.dto.NaverTokenResponse
import com.yapp.lettie.api.auth.client.dto.NaverUserInfoResponse
import com.yapp.lettie.api.auth.service.dto.AuthUserInfoDto
import com.yapp.lettie.common.error.ErrorMessages
import com.yapp.lettie.common.exception.ApiErrorException
import com.yapp.lettie.config.AuthNaverConfig
import com.yapp.lettie.util.RestClientUtil
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap

@Component
class NaverClient(
    private val authNaverClient: AuthNaverConfig,
    private val restClientUtil: RestClientUtil,
) {
    fun login(authorizationCode: String): AuthUserInfoDto {
        val tokenRequestParams =
            LinkedMultiValueMap<String, String>().apply {
                add("grant_type", authNaverClient.grantType)
                add("client_id", authNaverClient.clientId)
                add("client_secret", authNaverClient.clientSecret)
                add("state", authNaverClient.state)
                add("code", authorizationCode)
            }

        val tokenResponse =
            restClientUtil.post(
                url = authNaverClient.tokenUri,
                query = tokenRequestParams,
                responseType = NaverTokenResponse::class.java,
            ) ?: throw ApiErrorException(ErrorMessages.INVALID_INPUT_VALUE)

        val headers =
            mapOf(
                "Authorization" to tokenResponse.getBearerToken(),
            )

        val userInfoResponse =
            restClientUtil.get(
                url = authNaverClient.userInfoUri,
                headers = headers,
                responseType = NaverUserInfoResponse::class.java,
            ) ?: throw ApiErrorException(ErrorMessages.INTERNAL_SERVER_ERROR)

        return userInfoResponse.toDto()
    }
}

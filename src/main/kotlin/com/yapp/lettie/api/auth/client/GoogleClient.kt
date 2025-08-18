package com.yapp.lettie.api.auth.client

import com.yapp.lettie.api.auth.client.dto.GoogleTokenResponse
import com.yapp.lettie.api.auth.client.dto.GoogleUserInfoResponse
import com.yapp.lettie.api.auth.service.dto.AuthUserInfoDto
import com.yapp.lettie.common.error.ErrorMessages
import com.yapp.lettie.common.exception.ApiErrorException
import com.yapp.lettie.config.AuthGoogleConfig
import com.yapp.lettie.util.RestClientUtil
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Component
class GoogleClient(
    private val authoGoogleClient: AuthGoogleConfig,
    private val restClientUtil: RestClientUtil,
) {
    fun login(
        authorizationCode: String,
        redirectUrl: String,
        state: String? = null,
    ): AuthUserInfoDto {
        val tokenRequestParams =
            LinkedMultiValueMap<String, String>().apply {
                add("grant_type", authoGoogleClient.grantType)
                add("client_id", authoGoogleClient.clientId)
                add("client_secret", authoGoogleClient.clientSecret)
                add("redirect_uri", redirectUrl)
                add("code", URLDecoder.decode(authorizationCode, StandardCharsets.UTF_8))
                state?.let { add("state", it) }
            }

        val tokenResponse =
            restClientUtil.post(
                url = authoGoogleClient.tokenUri,
                query = tokenRequestParams,
                responseType = GoogleTokenResponse::class.java,
            ) ?: throw ApiErrorException(ErrorMessages.INVALID_INPUT_VALUE)

        val headers =
            mapOf(
                "Authorization" to tokenResponse.getBearerToken(),
            )

        val userInfoResponse =
            restClientUtil.get(
                url = authoGoogleClient.userUri,
                headers = headers,
                responseType = GoogleUserInfoResponse::class.java,
            ) ?: throw ApiErrorException(ErrorMessages.INTERNAL_SERVER_ERROR)

        return userInfoResponse.toDto()
    }
}

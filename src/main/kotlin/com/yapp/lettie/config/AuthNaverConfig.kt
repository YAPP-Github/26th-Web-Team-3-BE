package com.yapp.lettie.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class AuthNaverConfig {
    @Value("\${oauth.naver.client-id}")
    lateinit var clientId: String

    @Value("\${oauth.naver.client-secret}")
    lateinit var clientSecret: String

    @Value("\${oauth.naver.client-name}")
    lateinit var clientName: String

    @Value("\${oauth.naver.authorization-uri}")
    lateinit var authorizationUri: String

    @Value("\${oauth.naver.token-uri}")
    lateinit var tokenUri: String

    @Value("\${oauth.naver.user-info-uri}")
    lateinit var userInfoUri: String

    var grantType = "authorization_code"

    fun oauthUrl(
        url: String,
        state: String?,
    ): String {
        val baseUrl =
            authorizationUri +
                "?client_id=$clientId" +
                "&redirect_uri=$url" +
                "&response_type=code"

        return if (state != null) {
            "$baseUrl&state=$state"
        } else {
            baseUrl
        }
    }
}

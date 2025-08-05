package com.yapp.lettie.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class AuthKakaoConfig {
    @Value("\${oauth.kakao.client-id}")
    lateinit var clientId: String

    @Value("\${oauth.kakao.client-secret}")
    lateinit var clientSecret: String

    @Value("\${oauth.kakao.client-name}")
    lateinit var clientName: String

    @Value("\${oauth.kakao.authorization-uri}")
    lateinit var authorizationUri: String

    @Value("\${oauth.kakao.scope}")
    lateinit var scope: String

    @Value("\${oauth.kakao.token-uri}")
    lateinit var tokenUrl: String

    @Value("\${oauth.kakao.user-info-uri}")
    lateinit var userInfoUrl: String

    var grantType = "authorization_code"

    fun oauthUrl(url: String): String =
        authorizationUri +
            "?client_id=$clientId" +
            "&prompt=login" +
            "&redirect_uri=$url" +
            "&response_type=code" +
            "&scope=${scope.replace(",", "%20")}"
}

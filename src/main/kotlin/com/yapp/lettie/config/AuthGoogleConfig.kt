package com.yapp.lettie.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import java.net.URLEncoder

@Configuration
class AuthGoogleConfig {
    @Value("\${oauth.google.client-id}")
    lateinit var clientId: String

    @Value("\${oauth.google.client-secret}")
    lateinit var clientSecret: String

    @Value("\${oauth.google.redirect-uri}")
    lateinit var redirectUri: String

    @Value("\${oauth.google.client-name}")
    lateinit var clientName: String

    @Value("\${oauth.google.authorization-uri}")
    lateinit var authorizationUri: String

    @Value("\${oauth.google.scope}")
    lateinit var scope: String

    @Value("\${oauth.google.token-uri}")
    lateinit var tokenUri: String

    @Value("\${oauth.google.user-info-uri}")
    lateinit var userUri: String

    var grantType = "authorization_code"

    fun oauthUrl(): String =
        authorizationUri +
            "?client_id=$clientId" +
            "&redirect_uri=${URLEncoder.encode(redirectUri, "UTF-8")}" +
            "&response_type=code" +
            "&scope=${scope.replace(",", "%20")}"
}

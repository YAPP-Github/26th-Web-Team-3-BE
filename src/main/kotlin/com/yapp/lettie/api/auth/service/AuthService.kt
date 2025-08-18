package com.yapp.lettie.api.auth.service

import com.yapp.lettie.api.auth.client.GoogleClient
import com.yapp.lettie.api.auth.client.KakaoClient
import com.yapp.lettie.api.auth.client.NaverClient
import com.yapp.lettie.api.auth.component.JwtComponent
import com.yapp.lettie.api.auth.service.dto.JwtTokenDto
import com.yapp.lettie.config.AuthGoogleConfig
import com.yapp.lettie.config.AuthKakaoConfig
import com.yapp.lettie.config.AuthNaverConfig
import com.yapp.lettie.domain.user.OAuthProvider
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val authKakaoConfig: AuthKakaoConfig,
    private val authGoogleConfig: AuthGoogleConfig,
    private val authNaverConfig: AuthNaverConfig,
    private val kakaoClient: KakaoClient,
    private val googleClient: GoogleClient,
    private val naverClient: NaverClient,
    private val userLoginProcessor: UserLoginProcessor,
    private val jwtComponent: JwtComponent,
) {
    fun getKakaoLoginUrl(url: String): String = authKakaoConfig.oauthUrl(url)

    fun getGoogleLoginUrl(
        url: String,
        state: String?,
    ): String = authGoogleConfig.oauthUrl(url, state)

    fun getNaverLoginUrl(
        url: String,
        state: String?,
    ): String = authNaverConfig.oauthUrl(url, state)

    fun kakaoLogin(
        authorizationCode: String,
        redirectUrl: String,
    ): JwtTokenDto {
        val authUserInfoDto = kakaoClient.login(authorizationCode, redirectUrl)

        val user = userLoginProcessor.loginOrRegister(authUserInfoDto, OAuthProvider.KAKAO)
        val token: String = jwtComponent.create(user.id, user.role.key)

        return JwtTokenDto.of(token, user)
    }

    fun googleLogin(
        authorizationCode: String,
        redirectUrl: String,
    ): JwtTokenDto {
        val authUserInfoDto = googleClient.login(authorizationCode, redirectUrl)

        val user = userLoginProcessor.loginOrRegister(authUserInfoDto, OAuthProvider.GOOGLE)
        val token: String = jwtComponent.create(user.id, user.role.key)

        return JwtTokenDto.of(token, user)
    }

    fun naverLogin(
        authorizationCode: String,
        state: String?,
    ): JwtTokenDto {
        val authUserInfoDto = naverClient.login(authorizationCode, state)

        val user = userLoginProcessor.loginOrRegister(authUserInfoDto, OAuthProvider.NAVER)
        val token: String = jwtComponent.create(user.id, user.role.key)

        return JwtTokenDto.of(token, user)
    }
}

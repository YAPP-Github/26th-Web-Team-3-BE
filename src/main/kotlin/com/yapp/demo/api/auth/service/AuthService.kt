package com.yapp.demo.api.auth.service

import com.yapp.demo.api.auth.client.KakaoClient
import com.yapp.demo.api.auth.component.JwtComponent
import com.yapp.demo.api.auth.service.dto.JwtTokenDto
import com.yapp.demo.domain.user.entity.OAuthProvider
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val kakaoClient: KakaoClient,
    private val userLoginProcessor: UserLoginProcessor,
    private val jwtComponent: JwtComponent,
) {
    fun kakaoLogin(authorizationCode: String): JwtTokenDto {
        val kakaoUserInfoDto = kakaoClient.kakaoLogin(authorizationCode)

        val user = userLoginProcessor.loginOrRegister(kakaoUserInfoDto, OAuthProvider.KAKAO)
        val token: String = jwtComponent.create(user.id, user.role.key)

        return JwtTokenDto.of(token, user)
    }
}

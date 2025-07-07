package com.yapp.lettie.api.auth.service

import com.yapp.lettie.api.auth.service.dto.KakaoUserInfoDto
import com.yapp.lettie.domain.user.entity.OAuthProvider
import com.yapp.lettie.domain.user.entity.User
import com.yapp.lettie.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserLoginProcessor(
    private val userRepository: UserRepository,
) {
    @Transactional
    fun loginOrRegister(
        kakaoUserInfo: KakaoUserInfoDto,
        provider: OAuthProvider,
    ): User =
        userRepository.findByOauthIdAndProvider(kakaoUserInfo.id.toString(), provider)
            ?: userRepository.save(
                User(
                    oauthId = kakaoUserInfo.id.toString(),
                    provider = provider,
                    nickname = kakaoUserInfo.kakaoAccount?.profile?.nickname,
                ),
            )
}

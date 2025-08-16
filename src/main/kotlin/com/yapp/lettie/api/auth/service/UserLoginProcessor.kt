package com.yapp.lettie.api.auth.service

import com.yapp.lettie.api.auth.service.dto.AuthUserInfoDto
import com.yapp.lettie.domain.user.OAuthProvider
import com.yapp.lettie.domain.user.entity.User
import com.yapp.lettie.domain.user.repository.UserRepository
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserLoginProcessor(
    private val userRepository: UserRepository,
    private val cacheManager: RedisCacheManager,
) {
    @Transactional
    fun loginOrRegister(
        authUserInfoDto: AuthUserInfoDto,
        provider: OAuthProvider,
    ): User =
        userRepository.findByOauthIdAndProvider(authUserInfoDto.id, provider)
            ?: register(authUserInfoDto, provider)

    @Transactional
    fun register(
        authUserInfoDto: AuthUserInfoDto,
        provider: OAuthProvider,
    ): User {
        val saved =
            userRepository.save(
                User(
                    oauthId = authUserInfoDto.id,
                    email = authUserInfoDto.email,
                    provider = provider,
                    nickname = authUserInfoDto.name,
                ),
            )
        userRepository.flush()
        cacheManager.getCache("userTotalCount")?.clear()
        return saved
    }
}

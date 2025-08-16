package com.yapp.lettie.api.auth.service

import com.yapp.lettie.api.auth.service.dto.AuthUserInfoDto
import com.yapp.lettie.domain.user.OAuthProvider
import com.yapp.lettie.domain.user.entity.User
import com.yapp.lettie.domain.user.repository.UserRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserLoginProcessor(
    private val userRepository: UserRepository,
) {
    @Transactional
    fun loginOrRegister(
        authUserInfoDto: AuthUserInfoDto,
        provider: OAuthProvider,
    ): User =
        userRepository.findByOauthIdAndProvider(authUserInfoDto.id, provider)
            ?: register(authUserInfoDto, provider)

    @Transactional
    @CacheEvict(cacheNames = ["userTotalCount"], allEntries = true)
    fun register(
        authUserInfoDto: AuthUserInfoDto,
        provider: OAuthProvider,
    ): User {
        return userRepository.save(
            User(
                oauthId = authUserInfoDto.id,
                email = authUserInfoDto.email,
                provider = provider,
                nickname = authUserInfoDto.name,
            ),
        )
    }
}

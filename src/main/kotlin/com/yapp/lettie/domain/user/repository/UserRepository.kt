package com.yapp.lettie.domain.user.repository

import com.yapp.lettie.domain.user.OAuthProvider
import com.yapp.lettie.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByOauthIdAndProvider(
        oauthId: String,
        provider: OAuthProvider,
    ): User?
}

package com.yapp.lettie.domain.auth.repository

import com.yapp.lettie.domain.auth.entity.ApiAuth
import org.springframework.data.jpa.repository.JpaRepository

interface ApiAuthRepository : JpaRepository<ApiAuth, Long> {
    fun findByPathAndMethod(
        path: String,
        method: String,
    ): ApiAuth?
}

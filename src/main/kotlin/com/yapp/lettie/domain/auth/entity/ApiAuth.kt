package com.yapp.lettie.domain.auth.entity

import com.yapp.lettie.domain.BaseEntity
import com.yapp.lettie.domain.auth.AuthType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "api_auth")
class ApiAuth(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(name = "path", nullable = false)
    val path: String,
    @Column(name = "method", nullable = false)
    val method: String,
    @Column(name = "authType", nullable = false)
    @Enumerated(EnumType.STRING)
    val authType: AuthType,
) : BaseEntity()

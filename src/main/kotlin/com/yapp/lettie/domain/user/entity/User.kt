package com.yapp.lettie.domain.user.entity

import com.yapp.lettie.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(name = "oauth_id")
    val oauthId: String,
    @Column
    val email: String? = null,
    @Column
    val nickname: String? = null,
    @Column(name = "provider")
    @Enumerated(EnumType.STRING)
    val provider: OAuthProvider,
    @Column(name = "is_withdrawal", nullable = false)
    val isWithdrawal: Boolean = false,
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    val role: UserRole = UserRole.USER,
) : BaseEntity()

package com.yapp.lettie.domain.timecapsule.entity

import com.yapp.lettie.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "time_capsule_like")
data class TimeCapsuleLike(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "capsule_id", nullable = false)
    val capsuleId: Long,

    @Column(name = "liked_at", nullable = false)
    val likedAt: LocalDateTime = LocalDateTime.now(),
): BaseEntity()

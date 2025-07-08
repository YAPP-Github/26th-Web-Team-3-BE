package com.yapp.lettie.domain.timecapsule.repository

import com.yapp.lettie.domain.timecapsule.entity.TimeCapsuleUser
import org.springframework.data.jpa.repository.JpaRepository

interface TimeCapsuleUserRepository : JpaRepository<TimeCapsuleUser, Long> {
    fun findByUserId(userId: Long): List<TimeCapsuleUser>
    fun findByCapsuleId(capsuleId: Long): List<TimeCapsuleUser>
    fun findByUserIdAndCapsuleId(userId: Long, capsuleId: Long): TimeCapsuleUser?
}

package com.yapp.lettie.domain.timecapsule.repository

import com.yapp.lettie.domain.timecapsule.entity.TimeCapsuleUser
import org.springframework.data.jpa.repository.JpaRepository

interface TimeCapsuleUserRepository : JpaRepository<TimeCapsuleUser, Long> {
    fun findByUserId(userId: Long): List<TimeCapsuleUser>

    fun findByTimeCapsuleId(capsuleId: Long): List<TimeCapsuleUser>

    fun findByUserIdAndTimeCapsuleId(
        userId: Long,
        capsuleId: Long,
    ): TimeCapsuleUser?
}

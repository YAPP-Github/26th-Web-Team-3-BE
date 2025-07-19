package com.yapp.lettie.domain.timecapsule.repository

import com.yapp.lettie.domain.timecapsule.entity.TimeCapsuleUser
import org.springframework.data.jpa.repository.JpaRepository

interface TimeCapsuleUserRepository : JpaRepository<TimeCapsuleUser, Long> {
    fun countByTimeCapsuleId(capsuleId: Long): Long
}

package com.yapp.lettie.domain.timecapsule.repository

import com.yapp.lettie.domain.timecapsule.entity.TimeCapsuleLike
import org.springframework.data.jpa.repository.JpaRepository

interface TimeCapsuleLikeRepository : JpaRepository<TimeCapsuleLike, Long> {
    fun findByUserId(userId: Long): List<TimeCapsuleLike>

    fun findByTimeCapsuleId(capsuleId: Long): List<TimeCapsuleLike>
}

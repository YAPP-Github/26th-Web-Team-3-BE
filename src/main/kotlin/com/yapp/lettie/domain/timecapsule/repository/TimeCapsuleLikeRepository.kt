package com.yapp.lettie.domain.timecapsule.repository

import com.yapp.lettie.domain.timecapsule.entity.TimeCapsuleLike
import com.yapp.lettie.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface TimeCapsuleLikeRepository : JpaRepository<TimeCapsuleLike, Long> {
    fun findByUserIdAndTimeCapsuleId(
        userId: Long,
        capsule: Long,
    ): TimeCapsuleLike?

    fun findByUserAndIsLikedTrue(user: User): List<TimeCapsuleLike>
}

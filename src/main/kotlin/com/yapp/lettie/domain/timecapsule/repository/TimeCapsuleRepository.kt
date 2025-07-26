package com.yapp.lettie.domain.timecapsule.repository

import com.yapp.lettie.domain.timecapsule.entity.TimeCapsule
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface TimeCapsuleRepository : JpaRepository<TimeCapsule, Long> {
    fun findByInviteCode(inviteCode: String): TimeCapsule?

    fun findByCreatorIdOrderByCreatedAtDesc(
        creatorId: Long,
        pageable: Pageable,
    ): List<TimeCapsule>?
}

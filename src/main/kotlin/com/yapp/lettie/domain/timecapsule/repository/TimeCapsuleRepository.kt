package com.yapp.lettie.domain.timecapsule.repository

import com.yapp.lettie.domain.timecapsule.entity.TimeCapsule
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface TimeCapsuleRepository : JpaRepository<TimeCapsule, Long> {
    fun findByInviteCode(inviteCode: String): TimeCapsule?

    fun findByCreatorIdOrderByCreatedAtDesc(
        creatorId: Long,
        pageable: Pageable,
    ): List<TimeCapsule>?

    @Query(
        """
    SELECT tc FROM TimeCapsule tc
    LEFT JOIN Letter l ON l.timeCapsule = tc
    WHERE  tc.accessType = 'PUBLIC'
    GROUP BY tc.id
    ORDER BY COUNT(l.id) DESC, tc.createdAt DESC
    """,
    )
    fun findPopularTimeCapsules(pageable: Pageable): List<TimeCapsule>
}

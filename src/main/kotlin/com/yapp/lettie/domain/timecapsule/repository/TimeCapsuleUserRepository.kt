package com.yapp.lettie.domain.timecapsule.repository

import com.yapp.lettie.api.timecapsule.service.dto.RecipientRow
import com.yapp.lettie.domain.timecapsule.entity.TimeCapsuleUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface TimeCapsuleUserRepository : JpaRepository<TimeCapsuleUser, Long> {
    fun findAllByTimeCapsuleId(timeCapsuleId: Long): List<TimeCapsuleUser>

    fun countByTimeCapsuleId(capsuleId: Long): Int

    @Query(
        """
        SELECT tcu.timeCapsule.id, COUNT(tcu)
        FROM TimeCapsuleUser tcu
        WHERE tcu.timeCapsule.id IN :capsuleIds
        GROUP BY tcu.timeCapsule.id
    """,
    )
    fun getCountGroupedByCapsuleIds(
        @Param("capsuleIds") capsuleIds: List<Long>,
    ): List<Array<Any>>

    @Query(
        """
        select new com.yapp.lettie.api.timecapsule.service.reader.RecipientRow(
            tcu.timeCapsule.id,
            u.email,
            coalesce(u.nickname, '')
        )
        from TimeCapsuleUser tcu
        join tcu.user u
        where tcu.timeCapsule.id in :capsuleIds
    """,
    )
    fun findRecipientsByCapsuleIds(capsuleIds: List<Long>): List<RecipientRow>

    fun existsByUserIdAndTimeCapsuleId(
        userId: Long,
        capsuleId: Long,
    ): Boolean

    fun findByUserIdAndTimeCapsuleId(
        userId: Long,
        capsuleId: Long,
    ): TimeCapsuleUser?
}

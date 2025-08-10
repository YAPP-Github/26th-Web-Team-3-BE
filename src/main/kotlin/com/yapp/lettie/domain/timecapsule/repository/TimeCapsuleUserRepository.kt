package com.yapp.lettie.domain.timecapsule.repository

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
        SELECT tcu FROM TimeCapsuleUser tcu
        JOIN FETCH tcu.user
        WHERE tcu.timeCapsule.id IN :capsuleIds
        """,
    )
    fun findAllByCapsuleIdsFetchUser(
        @Param("capsuleIds") capsuleIds: List<Long>,
    ): List<TimeCapsuleUser>

    fun existsByUserIdAndTimeCapsuleId(
        userId: Long,
        capsuleId: Long,
    ): Boolean

    fun findByUserIdAndTimeCapsuleId(
        userId: Long,
        capsuleId: Long,
    ): TimeCapsuleUser?
}

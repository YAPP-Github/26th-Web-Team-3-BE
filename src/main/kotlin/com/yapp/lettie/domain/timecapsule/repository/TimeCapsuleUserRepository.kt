package com.yapp.lettie.domain.timecapsule.repository

import com.yapp.lettie.domain.timecapsule.entity.TimeCapsuleUser
import com.yapp.lettie.domain.timecapsule.entity.vo.TimeCapsuleUserStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface TimeCapsuleUserRepository : JpaRepository<TimeCapsuleUser, Long>, TimeCapsuleUserCustomRepository {
    fun findAllByTimeCapsuleId(timeCapsuleId: Long): List<TimeCapsuleUser>

    fun findAllByTimeCapsuleIdAndStatus(
        timeCapsuleId: Long,
        status: TimeCapsuleUserStatus,
    ): List<TimeCapsuleUser>

    fun countByTimeCapsuleId(capsuleId: Long): Int

    fun countByTimeCapsuleIdAndStatus(
        capsuleId: Long,
        status: TimeCapsuleUserStatus,
    ): Int

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
        SELECT tcu.timeCapsule.id, COUNT(tcu)
        FROM TimeCapsuleUser tcu
        WHERE tcu.timeCapsule.id IN :capsuleIds AND tcu.status = :status
        GROUP BY tcu.timeCapsule.id
    """,
    )
    fun getCountGroupedByCapsuleIdsAndStatus(
        @Param("capsuleIds") capsuleIds: List<Long>,
        @Param("status") status: TimeCapsuleUserStatus,
    ): List<Array<Any>>

    fun existsByUserIdAndTimeCapsuleId(
        userId: Long,
        capsuleId: Long,
    ): Boolean

    fun existsByUserIdAndTimeCapsuleIdAndStatus(
        userId: Long,
        capsuleId: Long,
        status: TimeCapsuleUserStatus,
    ): Boolean

    fun findByUserIdAndTimeCapsuleId(
        userId: Long,
        capsuleId: Long,
    ): TimeCapsuleUser?
}

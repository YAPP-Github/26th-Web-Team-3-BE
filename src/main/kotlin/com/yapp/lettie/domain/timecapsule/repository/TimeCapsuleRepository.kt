package com.yapp.lettie.domain.timecapsule.repository

import com.yapp.lettie.domain.timecapsule.entity.TimeCapsule
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface TimeCapsuleRepository :
    JpaRepository<TimeCapsule, Long>,
    TimeCapsuleCustomerRepository {
    @Query(
        """
        SELECT tc FROM TimeCapsule tc
        WHERE tc.openAt <= :now AND tc.openAt > :previousCheckTime
        """,
    )
    fun findAllCapsulesToOpen(
        @Param("previousCheckTime") previousCheckTime: LocalDateTime,
        @Param("now") now: LocalDateTime,
    ): List<TimeCapsule>
}

package com.yapp.lettie.domain.timecapsule.repository

import com.yapp.lettie.domain.timecapsule.entity.TimeCapsule
import com.yapp.lettie.domain.timecapsule.entity.vo.AccessType
import com.yapp.lettie.domain.timecapsule.entity.vo.CapsuleSort
import com.yapp.lettie.domain.timecapsule.entity.vo.MyCapsuleFilter
import com.yapp.lettie.domain.timecapsule.entity.vo.TimeCapsuleStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime

interface TimeCapsuleCustomRepository {
    fun getTimeCapsulesByStatus(
        type: TimeCapsuleStatus?,
        sort: CapsuleSort,
        now: LocalDateTime,
        pageable: Pageable,
    ): Page<TimeCapsule>

    fun findTimeCapsulesByTitle(
        title: String,
        accessType: AccessType,
        pageable: Pageable,
    ): Page<TimeCapsule>

    fun getMyTimeCapsules(
        userId: Long,
        filter: MyCapsuleFilter,
        sort: CapsuleSort,
        now: LocalDateTime,
        pageable: Pageable,
    ): Page<TimeCapsule>
}

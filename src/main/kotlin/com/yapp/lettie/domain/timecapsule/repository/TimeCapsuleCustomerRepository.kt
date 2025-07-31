package com.yapp.lettie.domain.timecapsule.repository

import com.yapp.lettie.domain.timecapsule.entity.TimeCapsule
import com.yapp.lettie.domain.timecapsule.entity.vo.TimeCapsuleStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime

interface TimeCapsuleCustomerRepository {
    fun getTimeCapsulesByStatus(
        type: TimeCapsuleStatus?,
        now: LocalDateTime,
        pageable: Pageable,
    ): Page<TimeCapsule>
}

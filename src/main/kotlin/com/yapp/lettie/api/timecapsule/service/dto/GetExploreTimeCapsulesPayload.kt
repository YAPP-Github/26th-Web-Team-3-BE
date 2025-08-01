package com.yapp.lettie.api.timecapsule.service.dto

import com.yapp.lettie.domain.timecapsule.entity.vo.TimeCapsuleStatus
import org.springframework.data.domain.Pageable

data class GetExploreTimeCapsulesPayload(
    val type: TimeCapsuleStatus?,
    val pageable: Pageable,
)

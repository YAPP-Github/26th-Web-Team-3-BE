package com.yapp.lettie.api.timecapsule.service.dto

import com.yapp.lettie.domain.timecapsule.entity.vo.AccessType
import java.time.LocalDate
import java.time.LocalDateTime

data class CreateTimeCapsulePayload(
    val title: String,
    val subtitle: String?,
    val accessType: AccessType,
    val openAt: LocalDateTime,
    val closedAt: LocalDate,
)

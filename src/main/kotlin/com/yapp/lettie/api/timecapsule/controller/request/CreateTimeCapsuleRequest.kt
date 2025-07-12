package com.yapp.lettie.api.timecapsule.controller.request

import com.yapp.lettie.api.timecapsule.service.dto.CreateTimeCapsulePayload
import com.yapp.lettie.domain.timecapsule.entity.vo.AccessType
import java.time.LocalDateTime

data class CreateTimeCapsuleRequest(
    val title: String,
    val subtitle: String?,
    val accessType: AccessType,
    val openAt: LocalDateTime,
    val closedAt: LocalDateTime,
) {
    fun to(): CreateTimeCapsulePayload {
        return CreateTimeCapsulePayload(
            title = title,
            subtitle = subtitle,
            accessType = accessType,
            openAt = openAt,
            closedAt = closedAt
        )
    }
}

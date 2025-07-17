package com.yapp.lettie.api.timecapsule.service.dto

import com.yapp.lettie.domain.timecapsule.entity.vo.TimeCapsuleStatus
import java.time.LocalDateTime

data class TimeCapsuleDetailPayload(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val openAt: LocalDateTime,
    val participantCount: Int,
    val likeCount: Int,
    val isLiked: Boolean? = null,
    val status: TimeCapsuleStatus,
    val remainingTime: RemainingTimePayload? = null,
)

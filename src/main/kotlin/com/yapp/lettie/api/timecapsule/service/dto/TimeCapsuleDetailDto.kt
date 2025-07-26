package com.yapp.lettie.api.timecapsule.service.dto

import com.yapp.lettie.domain.timecapsule.entity.vo.TimeCapsuleStatus
import java.time.LocalDateTime

data class TimeCapsuleDetailDto(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val openAt: LocalDateTime,
    val participantCount: Int,
    val letterCount: Int,
    val likeCount: Long,
    val isLiked: Boolean,
    val status: TimeCapsuleStatus,
    val remainingTime: RemainingTimeDto? = null,
    val isMine: Boolean,
)

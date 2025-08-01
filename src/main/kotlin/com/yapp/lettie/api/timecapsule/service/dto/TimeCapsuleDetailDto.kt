package com.yapp.lettie.api.timecapsule.service.dto

import com.yapp.lettie.domain.timecapsule.entity.vo.TimeCapsuleStatus
import java.time.LocalDateTime

data class TimeCapsuleDetailDto(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val openAt: LocalDateTime,
    val closedAt: LocalDateTime,
    val participantCount: Int,
    val letterCount: Int,
    val likeCount: Int,
    val isLiked: Boolean,
    val status: TimeCapsuleStatus,
    val remainingTime: RemainingTimeDto? = null,
    val isMine: Boolean,
    val inviteCode: String,
    val beadVideoUrl: String,
)

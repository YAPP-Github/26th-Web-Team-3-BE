package com.yapp.lettie.api.timecapsule.service.dto

import com.yapp.lettie.domain.timecapsule.entity.vo.AccessType

data class TimeCapsuleSummaryDto(
    val id: Long,
    val inviteCode: String,
    val title: String,
    val participantCount: Int,
    val letterCount: Int,
    val remainingStatus: RemainingStatusDto,
    val accessType: AccessType,
    // TODO: 썸네일 이미지 object key 추가
)

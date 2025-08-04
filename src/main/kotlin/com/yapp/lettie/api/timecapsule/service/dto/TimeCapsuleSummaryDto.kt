package com.yapp.lettie.api.timecapsule.service.dto

data class TimeCapsuleSummaryDto(
    val id: Long,
    val inviteCode: String,
    val title: String,
    val participantCount: Int,
    val letterCount: Int,
    val remainingStatus: RemainingStatusDto,
    // TODO: 썸네일 이미지 object key 추가
)

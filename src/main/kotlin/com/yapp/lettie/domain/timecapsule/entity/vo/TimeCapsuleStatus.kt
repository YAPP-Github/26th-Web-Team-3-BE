package com.yapp.lettie.domain.timecapsule.entity.vo

enum class TimeCapsuleStatus {
    WRITABLE, // 편지 작성 가능 (현재 < closedAt)
    WAITING_OPEN, // 작성 마감 이후 오픈 대기 (closedAt <= now < openAt)
    OPENED,
}

package com.yapp.lettie.domain.timecapsule.entity.vo

enum class TimeCapsuleUserStatus {
    /**
     * 한 번도 참여한 적이 없는 상태
     * 편지 작성을 통해 캡슐에 참여할 수 있음
     */
    NEVER_JOINED,

    /**
     * 현재 캡슐에 참여 중인 상태
     * 활성 참여자로 카운트됨
     */
    ACTIVE,

    /**
     * 캡슐에서 나간 상태
     * 편지 작성을 통한 재참여가 불가능함
     */
    LEFT,
}

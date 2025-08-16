package com.yapp.lettie.domain.timecapsule.dto

/**
 * 배치 메일 발송을 위한 내부 전용 수신자 DTO.
 * - email: PII 포함. 외부 API 응답 모델로 사용 금지.
 * - name: 닉네임이 없을 경우 빈 문자열("")로 내려갑니다.
 */
data class RecipientRow(
    val capsuleId: Long,
    val email: String,
    val name: String,
)

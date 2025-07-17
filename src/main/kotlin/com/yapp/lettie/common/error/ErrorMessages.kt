package com.yapp.lettie.common.error

import com.yapp.lettie.common.exception.ExtendedHttpStatus

enum class ErrorMessages(
    val status: ExtendedHttpStatus,
    val errorCode: Int,
    val message: String,
) {
    // common
    UNAUTHORIZED(ExtendedHttpStatus.UNAUTHORIZED, 1_001, "로그인이 필요합니다."),
    ACCESS_DENIED(ExtendedHttpStatus.FORBIDDEN, 1_002, "해당 요청에 대한 권한이 없습니다."),
    INTERNAL_SERVER_ERROR(ExtendedHttpStatus.INTERNAL_SERVER_ERROR, 1_003, "서버 오류가 발생했습니다. 잠시 후 다시 시도해 주세요."),
    INVALID_INPUT_VALUE(ExtendedHttpStatus.BAD_REQUEST, 1_004, "입력값이 올바르지 않습니다."),

    // auth
    INVALID_TOKEN(ExtendedHttpStatus.UNAUTHORIZED, 2_001, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(ExtendedHttpStatus.UNAUTHORIZED, 2_002, "토큰이 만료되었습니다."),

    // user
    USER_NOT_FOUND(ExtendedHttpStatus.NOT_FOUND, 3_001, "사용자를 찾을 수 없습니다."),

    // capsule
    CAPSULE_NOT_FOUND(ExtendedHttpStatus.NOT_FOUND, 4_001, "타임캡슐을 찾을 수 없습니다."),
    ALREADY_JOINED(ExtendedHttpStatus.BAD_REQUEST, 4_002, "이미 참여한 타임캡슐입니다."),
    CLOSED_TIME_CAPSULE(ExtendedHttpStatus.BAD_REQUEST, 4_003, "닫힌 타임캡슐에는 참여할 수 없습니다."),
    CAPSULE_LIKE_NOT_FOUND(ExtendedHttpStatus.NOT_FOUND, 4_004, "좋아요된 캡슐을 찾을 수 없습니다."),
}

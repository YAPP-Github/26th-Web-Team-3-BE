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
    NOT_FOUND(ExtendedHttpStatus.NOT_FOUND, 1_005, "요청하신 리소스를 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(ExtendedHttpStatus.METHOD_NOT_ALLOWED, 1_006, "허용되지 않은 HTTP 메서드입니다."),

    // auth
    INVALID_TOKEN(ExtendedHttpStatus.UNAUTHORIZED, 2_001, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(ExtendedHttpStatus.UNAUTHORIZED, 2_002, "토큰이 만료되었습니다."),
    REDIRECT_URL_REQUIRED(ExtendedHttpStatus.BAD_REQUEST, 2_003, "리다이렉트 URL이 필요합니다."),

    // user
    USER_NOT_FOUND(ExtendedHttpStatus.NOT_FOUND, 3_001, "사용자를 찾을 수 없습니다."),

    // capsule
    CAPSULE_NOT_FOUND(ExtendedHttpStatus.NOT_FOUND, 4_001, "타임캡슐을 찾을 수 없습니다."),
    ALREADY_JOINED(ExtendedHttpStatus.BAD_REQUEST, 4_002, "이미 참여한 타임캡슐입니다."),
    CLOSED_TIME_CAPSULE(ExtendedHttpStatus.BAD_REQUEST, 4_003, "닫힌 타임캡슐입니다."),
    CAPSULE_LIKE_NOT_FOUND(ExtendedHttpStatus.NOT_FOUND, 4_004, "좋아요된 캡슐을 찾을 수 없습니다."),
    NOT_OPENED_CAPSULE(ExtendedHttpStatus.BAD_REQUEST, 4_005, "타임캡슐이 열리지 않았습니다."),
    NOT_JOINED_TIME_CAPSULE(ExtendedHttpStatus.FORBIDDEN, 4_006, "타임캡슐에 참여하지 않았습니다."),
    INVALID_OPEN_AT(ExtendedHttpStatus.BAD_REQUEST, 4_007, "오픈 날짜가 현재 시간 이전입니다."),
    INVALID_CLOSED_AT(ExtendedHttpStatus.BAD_REQUEST, 4_008, "작성 마감 날짜가 오픈 날짜 이후입니다."),

    // file
    CAN_NOT_GET_PRESIGNED_URL(ExtendedHttpStatus.INTERNAL_SERVER_ERROR, 5_001, "Presigned URL을 가져올 수 없습니다."),
    FILE_NOT_FOUND(ExtendedHttpStatus.NOT_FOUND, 5_002, "파일을 찾을 수 없습니다."),

    // letter
    LETTER_NOT_FOUND(ExtendedHttpStatus.NOT_FOUND, 6_001, "편지를 찾을 수 없습니다."),
}

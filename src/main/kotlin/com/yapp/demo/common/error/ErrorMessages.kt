package com.yapp.demo.common.error

import com.yapp.demo.common.exception.ExtendedHttpStatus

enum class ErrorMessages(
    val status: ExtendedHttpStatus,
    val message: String,
) {
    // UNAUTHORIZED
    UNAUTHORIZED(ExtendedHttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),

    // Access Denied
    ACCESS_DENIED(ExtendedHttpStatus.FORBIDDEN, "해당 요청에 대한 권한이 없습니다."),

    // INTERNAL_SERVER_ERROR
    INTERNAL_SERVER_ERROR(ExtendedHttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다. 잠시 후 다시 시도해 주세요."),

    // 400
    INVALID_INPUT_VALUE(ExtendedHttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),
}

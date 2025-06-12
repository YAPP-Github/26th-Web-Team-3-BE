package com.yapp.demo.common.exception

class BadRequestException(
    code: String = "bad-request",
    cause: Throwable? = null
) : ErrorCodeResolvingApiErrorException(
    ExtendedHttpStatus.BAD_REQUEST,
    code,
    cause
)

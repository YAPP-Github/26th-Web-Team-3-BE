package com.yapp.lettie.common.exception

import com.yapp.lettie.common.error.ErrorMessages

class BadRequestException(
    cause: Throwable? = null,
) : ErrorCodeResolvingApiErrorException(ErrorMessages.INVALID_INPUT_VALUE, cause)

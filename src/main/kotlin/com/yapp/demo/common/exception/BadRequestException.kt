package com.yapp.demo.common.exception

import com.yapp.demo.common.error.ErrorMessages

class BadRequestException(
    cause: Throwable? = null
) : ErrorCodeResolvingApiErrorException(ErrorMessages.INVALID_INPUT_VALUE, cause)

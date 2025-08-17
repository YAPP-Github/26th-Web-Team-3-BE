package com.yapp.lettie.common.exception

import com.yapp.lettie.common.error.ErrorMessages

open class ErrorCodeResolvingApiErrorException : ApiErrorException {
    constructor(
        error: ErrorMessages,
        args: Array<out Any>? = null,
        data: Any? = null,
        cause: Throwable? = null,
    ) : super(ApiError.of(error, args, data), cause)

    constructor(
        error: ErrorMessages,
        cause: Throwable? = null,
    ) : this(error, null, null, cause)
}

package com.yapp.lettie.common.exception

import com.yapp.lettie.common.error.ErrorMessages

open class ApiErrorException(
    val error: ApiError,
    cause: Throwable? = null,
) : RuntimeException(cause) {
    constructor(
        errorMessage: ErrorMessages,
        args: Array<out Any>? = null,
        data: Any? = null,
        cause: Throwable? = null,
    ) : this(
        ApiError.of(errorMessage, args, data),
        cause,
    )
}

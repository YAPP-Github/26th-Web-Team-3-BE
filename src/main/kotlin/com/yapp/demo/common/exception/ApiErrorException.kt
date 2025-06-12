package com.yapp.demo.common.exception

open class ApiErrorException(
    val error: ApiError,
    cause: Throwable? = null
) : RuntimeException(cause) {

    constructor(
        status: ExtendedHttpStatus,
        code: String,
        args: Array<out Any>? = null,
        data: Any? = null,
        cause: Throwable? = null
    ) : this(
        ApiError.of(status, code, args, data),
        cause
    )
}

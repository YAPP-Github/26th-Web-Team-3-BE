package com.yapp.demo.common.exception

import com.yapp.demo.common.env.AppEnv
import com.yapp.demo.common.support.SpringContextHolder

open class ErrorCodeResolvingApiErrorException : ApiErrorException {

    companion object {
        private val appEnv: AppEnv by lazy {
            SpringContextHolder.getBean(AppEnv::class.java)
        }
    }

    constructor(
        statusCode: ExtendedHttpStatus,
        code: String,
        args: Array<out Any>? = null,
        data: Any? = null,
        cause: Throwable? = null
    ) : super(
        ApiError(
            appId = appEnv.getId(),
            status = statusCode,
            code = code,
            message = MessageResolver.resolve(code, args),
            data = data
        ),
        cause
    )

    constructor(
        statusCode: ExtendedHttpStatus,
        code: String,
        cause: Throwable? = null
    ) : this(statusCode, code, null, null, cause)
}

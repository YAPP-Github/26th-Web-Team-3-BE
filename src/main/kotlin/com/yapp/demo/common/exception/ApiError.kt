package com.yapp.demo.common.exception

import com.yapp.demo.common.env.AppEnv
import com.yapp.demo.common.support.SpringContextHolder

data class ApiError(
    val status: ExtendedHttpStatus,
    val code: String,
    val message: String?,
    val data: Any? = null
) {
    companion object {
        fun of(
            status: ExtendedHttpStatus,
            code: String,
            args: Array<out Any>? = null,
            data: Any? = null
        ): ApiError {
            val message = MessageResolver.resolve(code, args)
            return ApiError( status, code, message, data)
        }
    }
}

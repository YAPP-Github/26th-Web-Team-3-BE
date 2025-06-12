package com.yapp.demo.common.exception

import com.yapp.demo.common.env.AppEnv
import com.yapp.demo.common.support.SpringContextHolder

data class ApiError(
    val appId: String,
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
            val appId = SpringContextHolder.getBean(AppEnv::class.java).getId()
            val message = MessageResolver.resolve(code, args)
            return ApiError(appId, status, code, message, data)
        }
    }
}

package com.yapp.demo.common.exception

import com.yapp.demo.common.error.ErrorMessages

data class ApiError(
    val status: ExtendedHttpStatus,
    val code: String,
    val message: String?,
    val data: Any? = null,
) {
    companion object {
        fun of(
            error: ErrorMessages,
            args: Array<out Any>? = null,
            data: Any? = null,
        ): ApiError {
            val message = if (args != null) String.format(error.message, *args) else error.message
            return ApiError(error.status, error.status.code.toString(), message, data)
        }
    }
}

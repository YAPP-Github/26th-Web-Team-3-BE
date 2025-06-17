package com.yapp.demo.common.exception

enum class ExtendedHttpStatus(val code: Int) {
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND(404),
    CONFLICT(409),
    INTERNAL_SERVER_ERROR(500),
    SERVICE_UNAVAILABLE(503),
    ;

    fun isClientError() = code in 400..499

    fun isServerError() = code in 500..599

    fun isError() = isClientError() || isServerError()

    companion object {
        fun fromCode(code: Int): ExtendedHttpStatus =
            values().find { it.code == code } ?: throw IllegalArgumentException("Unknown code: $code")
    }

    override fun toString(): String = code.toString()
}

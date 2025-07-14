package com.yapp.lettie.domain.auth

enum class HttpMethod(
    val methodName: String,
) {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    PATCH("PATCH"),
    DELETE("DELETE"),
    HEAD("HEAD"),
    OPTIONS("OPTIONS"),
}

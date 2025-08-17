package com.yapp.lettie.domain.auth

enum class AuthType {
    REQUIRED, // 인증이 필요한 경우
    OPTIONAL, // 인증이 선택적인 경우
    NONE, // 인증이 필요하지 않은 경우
}

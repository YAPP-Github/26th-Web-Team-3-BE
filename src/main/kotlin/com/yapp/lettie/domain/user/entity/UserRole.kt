package com.yapp.lettie.domain.user.entity

enum class UserRole(
    val key: String,
    val value: String,
) {
    USER("ROLE_USER", "USER"),
    ADMIN("ROLE_ADMIN", "ADMIN"),
}

package com.yapp.lettie.domain.user

enum class UserRole(
    val key: String,
    val value: String,
) {
    GUEST("ROLE_GUEST", "GUEST"),
    USER("ROLE_USER", "USER"),
    ADMIN("ROLE_ADMIN", "ADMIN"),
}

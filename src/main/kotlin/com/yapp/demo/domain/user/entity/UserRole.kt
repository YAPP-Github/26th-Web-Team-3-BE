package com.yapp.demo.domain.user.entity

enum class UserRole(
    var key: String,
    var value: String,
) {
    USER("ROLE_USER", "USER"),
    ADMIN("ROLE_ADMIN", "ADMIN"),
}

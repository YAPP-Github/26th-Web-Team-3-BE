package com.yapp.lettie.api.auth.service.dto

data class AuthUserInfoDto(
    val id: String,
    val email: String,
    val name: String?,
)

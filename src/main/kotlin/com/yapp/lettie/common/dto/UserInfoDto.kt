package com.yapp.lettie.common.dto

import io.swagger.v3.oas.annotations.Hidden

@Hidden
data class UserInfoDto(
    val id: Long,
    val roles: List<String>,
)

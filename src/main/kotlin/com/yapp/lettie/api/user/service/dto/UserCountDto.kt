package com.yapp.lettie.api.user.service.dto

data class UserCountDto(
    val userTotalCount: Long,
) {
    companion object {
        fun from(userTotalCount: Long) =
            UserCountDto(
                userTotalCount = userTotalCount,
            )
    }
}

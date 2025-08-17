package com.yapp.lettie.api.user.controller.response

import com.yapp.lettie.api.user.service.dto.UserCountDto
import io.swagger.v3.oas.annotations.media.Schema

data class UserCountResponse(
    @Schema(description = "총 유저 수")
    val userTotalCount: Long,
) {
    companion object {
        fun of(userCountDto: UserCountDto) =
            UserCountResponse(
                userTotalCount = userCountDto.userTotalCount,
            )
    }
}

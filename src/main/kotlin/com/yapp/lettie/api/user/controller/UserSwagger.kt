package com.yapp.lettie.api.user.controller

import com.yapp.lettie.api.auth.annotation.LoginUser
import com.yapp.lettie.api.user.controller.response.UserResponse
import com.yapp.lettie.common.dto.ApiResponse
import com.yapp.lettie.common.dto.UserInfoDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity

@Tag(name = "user", description = "유저 API")
interface UserSwagger {
    @Operation(summary = "로그인 유저 정보 조회", description = "로그인 유저 정보를 조회합니다.")
    fun getMyInfo(
        @LoginUser userInfoDto: UserInfoDto,
    ): ResponseEntity<ApiResponse<UserResponse>>
}

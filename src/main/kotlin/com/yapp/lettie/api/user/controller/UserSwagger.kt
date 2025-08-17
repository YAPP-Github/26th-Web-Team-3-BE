package com.yapp.lettie.api.user.controller

import com.yapp.lettie.api.user.controller.response.UserCountResponse
import com.yapp.lettie.api.user.controller.response.UserResponse
import com.yapp.lettie.common.dto.ApiResponse
import com.yapp.lettie.common.dto.UserInfoPayload
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity

@Tag(name = "user", description = "유저 API")
interface UserSwagger {
    @Operation(summary = "로그인 유저 정보 조회", description = "로그인 유저 정보를 조회합니다.")
    fun getMyInfo(userInfoPayload: UserInfoPayload): ResponseEntity<ApiResponse<UserResponse>>

    @Operation(summary = "총 유저 수 정보 조회", description = "메인페이지에서 유저 수를 나타낼 때 사용합니다.")
    fun getUserTotalCount(): ResponseEntity<ApiResponse<UserCountResponse>>
}

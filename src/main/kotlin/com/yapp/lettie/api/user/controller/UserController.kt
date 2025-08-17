package com.yapp.lettie.api.user.controller

import com.yapp.lettie.api.auth.annotation.LoginUser
import com.yapp.lettie.api.user.controller.response.UserCountResponse
import com.yapp.lettie.api.user.controller.response.UserResponse
import com.yapp.lettie.api.user.service.UserService
import com.yapp.lettie.common.dto.ApiResponse
import com.yapp.lettie.common.dto.UserInfoPayload
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService,
) : UserSwagger {
    @GetMapping("/my-info")
    override fun getMyInfo(
        @LoginUser userInfoPayload: UserInfoPayload,
    ): ResponseEntity<ApiResponse<UserResponse>> =
        ResponseEntity.ok().body(
            ApiResponse.success(
                UserResponse.of(userService.getUserInfo(userInfoPayload.id)),
            ),
        )

    @GetMapping("/total-count")
    override fun getUserTotalCount(): ResponseEntity<ApiResponse<UserCountResponse>> =
        ResponseEntity.ok().body(
            ApiResponse.success(
                UserCountResponse.of(userService.getUserTotalCount()),
            ),
        )
}

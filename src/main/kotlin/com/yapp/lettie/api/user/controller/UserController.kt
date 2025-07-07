package com.yapp.lettie.api.user.controller

import com.yapp.lettie.api.user.controller.response.UserResponse
import com.yapp.lettie.api.user.service.UserService
import com.yapp.lettie.common.dto.ApiResponse
import com.yapp.lettie.common.dto.UserInfoDto
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
    override fun getMyInfo(userInfoDto: UserInfoDto): ResponseEntity<ApiResponse<UserResponse>> =
        ResponseEntity.ok().body(
            ApiResponse.success(
                UserResponse.of(userService.getUserInfo(userInfoDto.id)),
            ),
        )
}

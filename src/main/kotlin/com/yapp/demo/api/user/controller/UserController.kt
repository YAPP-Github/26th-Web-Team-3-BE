package com.yapp.demo.api.user.controller

import com.yapp.demo.api.user.controller.response.UserResponse
import com.yapp.demo.api.user.service.UserService
import com.yapp.demo.common.dto.ApiResponse
import com.yapp.demo.common.dto.UserInfoDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService,
) : UserEndPoint {
    @GetMapping("/my-info")
    override fun getMyInfo(userInfoDto: UserInfoDto): ResponseEntity<ApiResponse<UserResponse>> =
        ResponseEntity.ok().body(
            ApiResponse.success(
                UserResponse.of(userService.getUserInfo(userInfoDto.id)),
            ),
        )
}

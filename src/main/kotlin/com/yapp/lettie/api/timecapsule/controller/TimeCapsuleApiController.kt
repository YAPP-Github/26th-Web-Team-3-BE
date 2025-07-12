package com.yapp.lettie.api.timecapsule.controller

import com.yapp.lettie.api.auth.annotation.LoginUser
import com.yapp.lettie.api.timecapsule.controller.request.CreateTimeCapsuleRequest
import com.yapp.lettie.api.timecapsule.service.TimeCapsuleService
import com.yapp.lettie.common.dto.ApiResponse
import com.yapp.lettie.common.dto.UserInfoDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/capsule")
class TimeCapsuleApiController(
    private val timeCapsuleService: TimeCapsuleService,
) {
    fun create(
        @LoginUser userInfo: UserInfoDto,
        @RequestBody createTimeCapsuleRequest: CreateTimeCapsuleRequest,
    ): ResponseEntity<ApiResponse<Boolean>> {
        timeCapsuleService.createTimeCapsule(userInfo.id, createTimeCapsuleRequest.to())
        return ResponseEntity.ok().body(
            ApiResponse.success(true),
        )
    }
}

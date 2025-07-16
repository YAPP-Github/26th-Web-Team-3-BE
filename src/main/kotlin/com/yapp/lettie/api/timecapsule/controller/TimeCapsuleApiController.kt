package com.yapp.lettie.api.timecapsule.controller

import com.yapp.lettie.api.auth.annotation.LoginUser
import com.yapp.lettie.api.timecapsule.controller.request.CreateTimeCapsuleRequest
import com.yapp.lettie.api.timecapsule.controller.response.CreateTimeCapsuleResponse
import com.yapp.lettie.api.timecapsule.service.TimeCapsuleService
import com.yapp.lettie.api.timecapsule.swagger.TimeCapsuleSwagger
import com.yapp.lettie.common.dto.ApiResponse
import com.yapp.lettie.common.dto.UserInfoDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/capsule")
class TimeCapsuleApiController(
    private val timeCapsuleService: TimeCapsuleService,
) : TimeCapsuleSwagger {
    @PostMapping
    override fun create(
        @LoginUser userInfo: UserInfoDto,
        @RequestBody request: CreateTimeCapsuleRequest,
    ): ResponseEntity<ApiResponse<CreateTimeCapsuleResponse>> {
        return ResponseEntity.ok().body(
            ApiResponse.success(
                CreateTimeCapsuleResponse(
                    timeCapsuleService.createTimeCapsule(userInfo.id, request.to())
                ),
            )
        )
    }

    @PostMapping("/{capsuleId}/join")
    override fun join(
        @LoginUser userInfo: UserInfoDto,
        @PathVariable capsuleId: Long,
    ): ResponseEntity<ApiResponse<Boolean>> {
        timeCapsuleService.joinTimeCapsule(userInfo.id, capsuleId)
        return ResponseEntity.ok(ApiResponse.success(true))
    }
}

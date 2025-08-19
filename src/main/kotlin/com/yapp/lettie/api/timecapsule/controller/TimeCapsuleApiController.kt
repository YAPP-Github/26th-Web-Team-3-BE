package com.yapp.lettie.api.timecapsule.controller

import com.yapp.lettie.api.auth.annotation.LoginUser
import com.yapp.lettie.api.timecapsule.controller.request.CreateTimeCapsuleRequest
import com.yapp.lettie.api.timecapsule.controller.response.CreateTimeCapsuleResponse
import com.yapp.lettie.api.timecapsule.controller.swagger.TimeCapsuleSwagger
import com.yapp.lettie.api.timecapsule.service.TimeCapsuleService
import com.yapp.lettie.common.dto.ApiResponse
import com.yapp.lettie.common.dto.UserInfoPayload
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/capsules")
class TimeCapsuleApiController(
    private val timeCapsuleService: TimeCapsuleService,
) : TimeCapsuleSwagger {
    @PostMapping
    override fun create(
        @LoginUser userInfo: UserInfoPayload,
        @RequestBody request: CreateTimeCapsuleRequest,
    ): ResponseEntity<ApiResponse<CreateTimeCapsuleResponse>> =
        ResponseEntity.ok().body(
            ApiResponse.success(
                CreateTimeCapsuleResponse.from(timeCapsuleService.createTimeCapsule(userInfo.id, request.to())),
            ),
        )

    @PutMapping("/{capsuleId}/like")
    override fun like(
        @LoginUser userInfo: UserInfoPayload,
        @PathVariable capsuleId: Long,
    ): ResponseEntity<ApiResponse<Boolean>> {
        timeCapsuleService.like(userInfo.id, capsuleId)
        return ResponseEntity.ok(ApiResponse.success(true))
    }

    @DeleteMapping("/{capsuleId}/like")
    override fun unlike(
        @LoginUser userInfo: UserInfoPayload,
        @PathVariable capsuleId: Long,
    ): ResponseEntity<ApiResponse<Boolean>> {
        timeCapsuleService.unlike(userInfo.id, capsuleId)
        return ResponseEntity.ok(ApiResponse.success(true))
    }

    @DeleteMapping("/{capsuleId}/leave")
    override fun leave(
        @LoginUser userInfo: UserInfoPayload,
        @PathVariable capsuleId: Long,
    ): ResponseEntity<ApiResponse<Boolean>> {
        timeCapsuleService.leaveTimeCapsule(userInfo.id, capsuleId)
        return ResponseEntity.ok(ApiResponse.success(true))
    }

    @Deprecated("편지 작성 시 자동 참여 처리로 인해 더 이상 사용되지 않습니다. 추후 제거 예정입니다.")
    @PostMapping("/{capsuleId}/join")
    override fun join(
        @LoginUser userInfo: UserInfoPayload,
        @PathVariable capsuleId: Long,
    ): ResponseEntity<ApiResponse<Boolean>> {
        timeCapsuleService.joinTimeCapsule(userInfo.id, capsuleId)
        return ResponseEntity.ok(ApiResponse.success(true))
    }
}

package com.yapp.lettie.api.timecapsule.controller.swagger

import com.yapp.lettie.api.timecapsule.controller.request.CreateTimeCapsuleRequest
import com.yapp.lettie.api.timecapsule.controller.response.CreateTimeCapsuleResponse
import com.yapp.lettie.api.timecapsule.controller.response.ToggleTimeCapsuleLikeResponse
import com.yapp.lettie.common.dto.ApiResponse
import com.yapp.lettie.common.dto.UserInfoDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping

@Tag(name = "TimeCapsule Make", description = "타임캡슐 Make API (생성, 참여, 좋아요)")
@RequestMapping("/api/v1/capsule")
interface TimeCapsuleSwagger {
    @Operation(
        summary = "타임캡슐 생성",
        description = "로그인한 사용자가 새로운 타임캡슐을 생성합니다.",
    )
    fun create(
        @Parameter(hidden = true) userInfo: UserInfoDto,
        request: CreateTimeCapsuleRequest,
    ): ResponseEntity<ApiResponse<CreateTimeCapsuleResponse>>

    @Operation(
        summary = "타임캡슐 참여",
        description = "캡슐 ID를 통해 타임캡슐에 참여합니다.",
    )
    fun join(
        @Parameter(hidden = true) userInfo: UserInfoDto,
        capsuleId: Long,
    ): ResponseEntity<ApiResponse<Boolean>>

    @Operation(
        summary = "타임캡슐 좋아요 토글",
        description = "좋아요를 누르거나 취소합니다. 응답값이 true면 좋아요 상태, false면 좋아요 취소 상태입니다.",
    )
    fun toggleLike(
        @Parameter(hidden = true) userInfo: UserInfoDto,
        capsuleId: Long,
    ): ResponseEntity<ApiResponse<ToggleTimeCapsuleLikeResponse>>
}

package com.yapp.lettie.api.timecapsule.swagger

import com.yapp.lettie.api.timecapsule.controller.request.CreateTimeCapsuleRequest
import com.yapp.lettie.common.dto.ApiResponse
import com.yapp.lettie.common.dto.UserInfoDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@Tag(name = "TimeCapsule", description = "타임캡슐 관련 API")
@RequestMapping("/api/v1/capsule")
interface TimeCapsuleSwagger {
    @Operation(
        summary = "타임캡슐 생성",
        description = "로그인한 사용자가 새로운 타임캡슐을 생성합니다.",
    )
    @PostMapping
    fun create(
        @Parameter(hidden = true) userInfo: UserInfoDto,
        request: CreateTimeCapsuleRequest,
    ): ResponseEntity<ApiResponse<Boolean>>
}

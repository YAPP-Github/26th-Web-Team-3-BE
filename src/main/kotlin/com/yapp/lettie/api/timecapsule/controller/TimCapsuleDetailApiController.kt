package com.yapp.lettie.api.timecapsule.controller

import com.yapp.lettie.api.auth.annotation.LoginUser
import com.yapp.lettie.api.timecapsule.controller.response.TimeCapsuleDetailResponse
import com.yapp.lettie.api.timecapsule.controller.swagger.TimeCapsuleDetailSwagger
import com.yapp.lettie.api.timecapsule.service.TimeCapsuleDetailService
import com.yapp.lettie.common.dto.ApiResponse
import com.yapp.lettie.common.dto.UserInfoDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/capsule")
class TimCapsuleDetailApiController(
    private val timeCapsuleDetailService: TimeCapsuleDetailService,
) : TimeCapsuleDetailSwagger {
    @GetMapping("/{capsuleId}")
    override fun getCapsuleDetail(
        @LoginUser UserInfo: UserInfoDto?,
        @PathVariable capsuleId: Long,
    ): ResponseEntity<ApiResponse<TimeCapsuleDetailResponse>> =
        ResponseEntity.ok(
            ApiResponse.success(
                TimeCapsuleDetailResponse.from(
                    timeCapsuleDetailService.getTimeCapsuleDetail(
                        capsuleId = capsuleId,
                        userId = UserInfo?.id,
                    ),
                ),
            ),
        )
}

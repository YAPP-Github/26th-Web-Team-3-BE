package com.yapp.lettie.api.timecapsule.controller

import com.yapp.lettie.api.auth.annotation.LoginUser
import com.yapp.lettie.api.timecapsule.controller.response.TimeCapsuleDetailResponse
import com.yapp.lettie.api.timecapsule.controller.response.TimeCapsuleSummaryResponse
import com.yapp.lettie.api.timecapsule.controller.swagger.TimeCapsuleDetailSwagger
import com.yapp.lettie.api.timecapsule.service.TimeCapsuleDetailService
import com.yapp.lettie.common.dto.ApiResponse
import com.yapp.lettie.common.dto.UserInfoDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/capsule")
class TimeCapsuleDetailApiController(
    private val timeCapsuleDetailService: TimeCapsuleDetailService,
) : TimeCapsuleDetailSwagger {
    @GetMapping("/{capsuleId}")
    override fun getCapsuleDetail(
        @LoginUser userInfo: UserInfoDto,
        @PathVariable capsuleId: Long,
    ): ResponseEntity<ApiResponse<TimeCapsuleDetailResponse>> {
        val detailDto = timeCapsuleDetailService.getTimeCapsuleDetail(capsuleId, userInfo.id)
        return ResponseEntity.ok(
            ApiResponse.success(TimeCapsuleDetailResponse.from(detailDto)),
        )
    }

    @GetMapping("/my")
    override fun getMyTimeCapsules(
        @LoginUser userInfo: UserInfoDto,
        @RequestParam limit: Int,
    ): ResponseEntity<ApiResponse<List<TimeCapsuleSummaryResponse>>> =
        ResponseEntity.ok(
            ApiResponse.success(
                timeCapsuleDetailService.getMyTimeCapsules(userInfo.id, limit)
                    .map { TimeCapsuleSummaryResponse.from(it) },
            ),
        )

    @GetMapping("/popular")
    override fun getPopularTimeCapsules(
        @RequestParam limit: Int,
    ): ResponseEntity<ApiResponse<List<TimeCapsuleSummaryResponse>>> =
        ResponseEntity.ok(
            ApiResponse.success(timeCapsuleDetailService.getPopularTimeCapsules(limit)
                .map { TimeCapsuleSummaryResponse.from(it) }
            )
        )
}

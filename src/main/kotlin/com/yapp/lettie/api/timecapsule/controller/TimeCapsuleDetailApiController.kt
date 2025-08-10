package com.yapp.lettie.api.timecapsule.controller

import com.yapp.lettie.api.auth.annotation.LoginUser
import com.yapp.lettie.api.timecapsule.controller.response.TimeCapsuleDetailResponse
import com.yapp.lettie.api.timecapsule.controller.response.TimeCapsuleSummariesResponse
import com.yapp.lettie.api.timecapsule.controller.response.TimeCapsuleSummaryResponse
import com.yapp.lettie.api.timecapsule.controller.swagger.TimeCapsuleDetailSwagger
import com.yapp.lettie.api.timecapsule.service.TimeCapsuleDetailService
import com.yapp.lettie.api.timecapsule.service.dto.ExploreMyTimeCapsulesPayload
import com.yapp.lettie.api.timecapsule.service.dto.ExploreTimeCapsulesPayload
import com.yapp.lettie.api.timecapsule.service.dto.SearchTimeCapsulesPayload
import com.yapp.lettie.common.dto.ApiResponse
import com.yapp.lettie.common.dto.UserInfoPayload
import com.yapp.lettie.domain.timecapsule.entity.vo.CapsuleSort
import com.yapp.lettie.domain.timecapsule.entity.vo.MyCapsuleFilter
import com.yapp.lettie.domain.timecapsule.entity.vo.TimeCapsuleStatus
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/capsules")
class TimeCapsuleDetailApiController(
    private val timeCapsuleDetailService: TimeCapsuleDetailService,
) : TimeCapsuleDetailSwagger {
    @GetMapping("/{capsuleId}")
    override fun getCapsuleDetail(
        @LoginUser userInfo: UserInfoPayload?,
        @PathVariable capsuleId: Long,
    ): ResponseEntity<ApiResponse<TimeCapsuleDetailResponse>> {
        val detailDto = timeCapsuleDetailService.getTimeCapsuleDetail(capsuleId, userInfo?.id)
        return ResponseEntity.ok(
            ApiResponse.success(TimeCapsuleDetailResponse.from(detailDto)),
        )
    }

    @GetMapping("/my")
    override fun getMyTimeCapsules(
        @LoginUser userInfo: UserInfoPayload,
        @RequestParam(value = "filter", defaultValue = "ALL")
        filter: MyCapsuleFilter,
        @RequestParam(value = "sort", defaultValue = "DEFAULT")
        sort: CapsuleSort,
        pageable: Pageable,
    ): ResponseEntity<ApiResponse<TimeCapsuleSummariesResponse>> {
        val payload =
            ExploreMyTimeCapsulesPayload.of(
                filter,
                sort,
                pageable,
            )

        val capsules =
            timeCapsuleDetailService.getMyTimeCapsules(
                userId = userInfo.id,
                payload = payload,
            )

        return ResponseEntity.ok(
            ApiResponse.success(
                TimeCapsuleSummariesResponse.from(capsules),
            ),
        )
    }

    @GetMapping("/popular")
    override fun getPopularTimeCapsules(
        pageable: Pageable,
    ): ResponseEntity<ApiResponse<List<TimeCapsuleSummaryResponse>>> =
        ResponseEntity.ok(
            ApiResponse.success(
                timeCapsuleDetailService
                    .getPopularTimeCapsules(pageable)
                    .timeCapsules
                    .map { TimeCapsuleSummaryResponse.from(it) },
            ),
        )

    @GetMapping("/explore")
    override fun exploreTimeCapsules(
        @RequestParam
        type: TimeCapsuleStatus?,
        @RequestParam(value = "sort", defaultValue = "DEFAULT")
        sort: CapsuleSort,
        pageable: Pageable,
    ): ResponseEntity<ApiResponse<TimeCapsuleSummariesResponse>> {
        val payload =
            ExploreTimeCapsulesPayload.of(
                type,
                sort,
                pageable = pageable,
            )

        val capsules = timeCapsuleDetailService.exploreTimeCapsules(payload)

        return ResponseEntity.ok(
            ApiResponse.success(
                TimeCapsuleSummariesResponse.from(capsules),
            ),
        )
    }

    @GetMapping("/search")
    override fun searchTimeCapsules(
        @RequestParam keyword: String,
        pageable: Pageable,
    ): ResponseEntity<ApiResponse<TimeCapsuleSummariesResponse>> {
        val payload =
            SearchTimeCapsulesPayload(
                keyword = keyword,
                pageable = pageable,
            )

        val capsules = timeCapsuleDetailService.searchTimeCapsules(payload)

        return ResponseEntity.ok(
            ApiResponse.success(
                TimeCapsuleSummariesResponse.from(capsules),
            ),
        )
    }
}

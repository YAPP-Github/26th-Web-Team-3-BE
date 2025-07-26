package com.yapp.lettie.api.timecapsule.controller.swagger

import com.yapp.lettie.api.timecapsule.controller.response.TimeCapsuleDetailResponse
import com.yapp.lettie.api.timecapsule.controller.response.TimeCapsuleSummaryResponse
import com.yapp.lettie.common.dto.ApiResponse
import com.yapp.lettie.common.dto.UserInfoPayload
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping

@Tag(name = "TimeCapsule Detail", description = "타임캡슐 Detail API (상세보기, 리스트 목록 반환)")
@RequestMapping("/api/v1/capsule")
interface TimeCapsuleDetailSwagger {
    @Operation(
        summary = "타임캡슐 상세 조회",
        description =
            """
        캡슐의 기본 정보와 오픈일시, 참여자 수, 좋아요 여부, 상태 및 남은 시간을 조회합니다.
        로그인하지 않은 사용자도 접근 가능하며, 로그인 사용자일 경우 좋아요 여부(liked)가 포함됩니다.

        <status & remainingTime>
        - 작성 마감 시점이 지나지 않은 경우: status = WRITABLE, remainingTime = closedAt - 현재
        - 작성이 마감되고 캡슐 오픈까지 남은 경우: status = WAITING_OPEN, remainingTime = openedAt - 현재
        - 타임캡슐이 오픈된 경우: status = OPENED, remainingTime = openedAt
        """,
    )
    fun getCapsuleDetail(
        @Parameter(hidden = true) userInfo: UserInfoPayload,
        capsuleId: Long,
    ): ResponseEntity<ApiResponse<TimeCapsuleDetailResponse>>

    @Operation(
        summary = "메인페이지 내가 만든 캡슐 조회 (로그인 시에만)",
        description = "내가 가장 최근에 만든 타임 캡슐 리스트를 불러온다.",
    )
    fun getMyTimeCapsules(
        @Parameter(hidden = true) userInfo: UserInfoPayload,
        @Parameter(description = "불러올 개수 (default: 10)") limit: Int = 10,
    ): ResponseEntity<ApiResponse<List<TimeCapsuleSummaryResponse>>>

    @Operation(
        summary = "메인페이지 인기 캡슐 조회 (비로그인 가능)",
        description = """
            메인페이지 인기 캡슐 조회
            1. limit을 12로 지정하여 메인페이지 인기 캡슐을 조회한다.
            2.<더보기> 버튼을 눌렀을 때 limit을 60으로 지정하여 인기 캡슐을 조회한다.
        """,
    )
    fun getPopularTimeCapsules(
        @Parameter(description = "불러올 개수 (default: 12)") limit: Int = 12,
    ): ResponseEntity<ApiResponse<List<TimeCapsuleSummaryResponse>>>
}

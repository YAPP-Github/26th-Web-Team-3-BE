package com.yapp.lettie.api.timecapsule.controller.swagger

import com.yapp.lettie.api.timecapsule.controller.response.TimeCapsuleDetailResponse
import com.yapp.lettie.api.timecapsule.controller.response.TimeCapsuleSummariesResponse
import com.yapp.lettie.api.timecapsule.controller.response.TimeCapsuleSummaryResponse
import com.yapp.lettie.common.dto.ApiResponse
import com.yapp.lettie.common.dto.UserInfoPayload
import com.yapp.lettie.domain.timecapsule.entity.vo.TimeCapsuleStatus
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.Parameters
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.NotBlank
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Pageable
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
        userInfo: UserInfoPayload,
        capsuleId: Long,
    ): ResponseEntity<ApiResponse<TimeCapsuleDetailResponse>>

    @Operation(
        summary = "메인페이지 내가 만든 캡슐 조회 (로그인 시에만)",
        description = """
            [메인페이지 내가 만든 캡슐 조회 호출 방식]
            1. 로그인 시에만 내가 만든 캡슐을 조회할 수 있다.
            2. size를 10으로 지정하여 메인페이지 내가 만든 캡슐을 조회한다.
            3. 정렬 방식은 생성일자 (최근)순으로 정렬하여 반환한다.

            [응답 구조]
            remaingStatus.type
            - WRITABLE, WAITING_OPEN인 경우 남은 시간을 day, hours, minutes로 반환한다.
            - OPENED인 경우 오픈 날짜와 '오픈 완료'를 반환한다.
        """,
    )
    @Parameters(
        Parameter(
            name = "page",
            description = "페이지 번호 (0부터 시작)",
            `in` = ParameterIn.QUERY,
        ),
        Parameter(
            name = "size",
            description = "페이지 크기",
            `in` = ParameterIn.QUERY,
        ),
        Parameter(
            name = "sort",
            description = "정렬 조건 (예: id,desc 또는 id,asc)",
            `in` = ParameterIn.QUERY,
        ),
    )
    fun getMyTimeCapsules(
        userInfo: UserInfoPayload,
        @ParameterObject pageable: Pageable,
    ): ResponseEntity<ApiResponse<List<TimeCapsuleSummaryResponse>>>

    @Operation(
        summary = "<< 보류 >> 편지 수 인기 캡슐 조회 (비로그인 가능)",
        description = """
            [탐색페이지 인기 캡슐 조회 호출 방식]
            1. size를 12로 지정하여 탐색페이지 인기 캡슐을 조회한다.
            2. <더보기> 버튼을 눌렀을 때 limit을 60으로 넘겨서 인기 캡슐을 조회한다.
            3. 정렬 방식은 편지 수 -> 생성일자 (최근)순으로 정렬하여 반환한다.

            [응답 구조]
            remaingStatus.type
            - WRITABLE, WAITING_OPEN인 경우 남은 시간을 day, hours, minutes로 반환한다.
            - OPENED인 경우 오픈 날짜와 '오픈 완료'를 반환한다.
        """,
    )
    @Parameters(
        Parameter(
            name = "page",
            description = "페이지 번호 (0부터 시작)",
            `in` = ParameterIn.QUERY,
        ),
        Parameter(
            name = "size",
            description = "페이지 크기",
            `in` = ParameterIn.QUERY,
        ),
        Parameter(
            name = "sort",
            description = "정렬 조건 (예: id,desc 또는 id,asc)",
            `in` = ParameterIn.QUERY,
        ),
    )
    fun getPopularTimeCapsules(
        @ParameterObject pageable: Pageable,
    ): ResponseEntity<ApiResponse<List<TimeCapsuleSummaryResponse>>>

    @Operation(
        summary = "타임캡슐 리스트 조회 (비로그인 가능)",
        description =
            """
        타임캡슐 리스트를 조회합니다. 타입에 따라 필터링이 가능합니다.
        페이지네이션과 정렬 기능을 지원합니다.

        <타입별 필터링>
        - WRITABLE: 작성 가능한 캡슐
        - WAITING_OPEN: 오픈 대기 중인 캡슐
        - OPENED: 오픈된 캡슐
        """,
    )
    @Parameters(
        Parameter(
            name = "page",
            description = "페이지 번호 (0부터 시작)",
            `in` = ParameterIn.QUERY,
        ),
        Parameter(
            name = "size",
            description = "페이지 크기",
            `in` = ParameterIn.QUERY,
        ),
        Parameter(
            name = "sort",
            description = "정렬 조건 (예: id,desc 또는 id,asc)",
            `in` = ParameterIn.QUERY,
        ),
    )
    fun exploreTimeCapsules(
        type: TimeCapsuleStatus?,
        @ParameterObject pageable: Pageable,
    ): ResponseEntity<ApiResponse<TimeCapsuleSummariesResponse>>

    @Operation(
        summary = "타임캡슐 검색 (비로그인 가능)",
        description =
            """
        타임캡슐을 키워드로 검색합니다. 페이지네이션과 정렬 기능을 지원합니다.
        검색어는 타임캡슐 제목, 설명, 작성자 이름 등을 포함합니다.
        """,
    )
    @Parameters(
        Parameter(
            name = "page",
            description = "페이지 번호 (0부터 시작)",
            `in` = ParameterIn.QUERY,
        ),
        Parameter(
            name = "size",
            description = "페이지 크기",
            `in` = ParameterIn.QUERY,
        ),
        Parameter(
            name = "sort",
            description = "정렬 조건 (예: id,desc 또는 id,asc)",
            `in` = ParameterIn.QUERY,
        ),
    )
    fun searchTimeCapsules(
        @NotBlank keyword: String,
        @ParameterObject pageable: Pageable,
    ): ResponseEntity<ApiResponse<TimeCapsuleSummariesResponse>>
}

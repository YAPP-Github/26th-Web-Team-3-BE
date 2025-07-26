package com.yapp.lettie.api.letter.controller.swagger

import com.yapp.lettie.api.letter.controller.request.CreateLetterRequest
import com.yapp.lettie.api.letter.controller.response.CreateLetterResponse
import com.yapp.lettie.api.letter.controller.response.LetterResponse
import com.yapp.lettie.api.letter.controller.response.LettersResponse
import com.yapp.lettie.common.dto.ApiResponse
import com.yapp.lettie.common.dto.UserInfoPayload
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.Parameters
import io.swagger.v3.oas.annotations.enums.ParameterIn
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity

interface LetterSwagger {
    @Operation(
        summary = "타임캡슐 편지 작성",
        description = "캡슐에 편지를 작성합니다.",
    )
    fun writeLetter(
        userInfo: UserInfoPayload,
        request: CreateLetterRequest,
    ): ResponseEntity<ApiResponse<CreateLetterResponse>>

    @Operation(
        summary = "타임캡슐 편지 리스트 조회",
        description = "타임캡슐에 작성된 편지들을 조회합니다.",
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
    fun readLetters(
        userInfo: UserInfoPayload,
        capsuleId: Long,
        @ParameterObject pageable: Pageable,
    ): ResponseEntity<ApiResponse<LettersResponse>>

    @Operation(
        summary = "타임캡슐 편지 상세 조회",
        description = "타임캡슐에 작성된 편지를 조회합니다.",
    )
    fun readLetter(
        userInfo: UserInfoPayload,
        letterId: Long,
    ): ResponseEntity<ApiResponse<LetterResponse>>
}

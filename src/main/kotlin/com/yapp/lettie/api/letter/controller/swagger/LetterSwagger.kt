package com.yapp.lettie.api.letter.controller.swagger

import com.yapp.lettie.api.auth.annotation.LoginUser
import com.yapp.lettie.api.letter.controller.request.CreateLetterRequest
import com.yapp.lettie.api.letter.controller.response.CreateLetterResponse
import com.yapp.lettie.common.dto.ApiResponse
import com.yapp.lettie.common.dto.UserInfoDto
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody

interface LetterSwagger {
    @Operation(
        summary = "타임캡슐 편지 작성",
        description = "캡슐에 편지를 작성합니다.",
    )
    fun writeLetter(
        @LoginUser userInfo: UserInfoDto,
        @RequestBody request: CreateLetterRequest,
    ): ResponseEntity<ApiResponse<CreateLetterResponse>>
}

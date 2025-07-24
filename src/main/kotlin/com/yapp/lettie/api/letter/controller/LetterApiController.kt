package com.yapp.lettie.api.letter.controller

import com.yapp.lettie.api.auth.annotation.LoginUser
import com.yapp.lettie.api.letter.controller.request.CreateLetterRequest
import com.yapp.lettie.api.letter.controller.response.CreateLetterResponse
import com.yapp.lettie.api.letter.controller.swagger.LetterSwagger
import com.yapp.lettie.api.letter.service.LetterService
import com.yapp.lettie.common.dto.ApiResponse
import com.yapp.lettie.common.dto.UserInfoDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/letters")
class LetterApiController(
    private val letterService: LetterService,
) : LetterSwagger {
    @PostMapping()
    override fun writeLetter(
        @LoginUser userInfo: UserInfoDto,
        @RequestBody request: CreateLetterRequest,
    ): ResponseEntity<ApiResponse<CreateLetterResponse>> {
        val payload = request.toPayload()

        return ResponseEntity.ok(
            ApiResponse.success(
                CreateLetterResponse(
                    letterService.writeLetter(userInfo.id, payload),
                ),
            ),
        )
    }
}

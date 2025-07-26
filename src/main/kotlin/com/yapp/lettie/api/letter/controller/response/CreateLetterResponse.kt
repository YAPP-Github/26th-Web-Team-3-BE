package com.yapp.lettie.api.letter.controller.response

import io.swagger.v3.oas.annotations.media.Schema

data class CreateLetterResponse(
    @Schema(description = "편지 ID", example = "1")
    val id: Long,
) {
    companion object {
        fun of(id: Long): CreateLetterResponse = CreateLetterResponse(id = id)
    }
}

package com.yapp.lettie.api.letter.controller.response

import io.swagger.v3.oas.annotations.media.Schema

data class CreateLetterResponse(
    @Schema(description = "letter Id", example = "1L")
    val id: Long,
)

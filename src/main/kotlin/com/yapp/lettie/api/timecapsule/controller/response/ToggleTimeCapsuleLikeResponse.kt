package com.yapp.lettie.api.timecapsule.controller.response

import io.swagger.v3.oas.annotations.media.Schema

data class ToggleTimeCapsuleLikeResponse(
    @Schema(description = "timeCapsule Like", example = "true")
    val isLiked: Boolean,
)

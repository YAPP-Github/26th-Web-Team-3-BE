package com.yapp.demo.api.auth.controller.response

import io.swagger.v3.oas.annotations.media.Schema

data class OAuthUrlResponse(
    @Schema(description = "oauth url", example = "https://example.com/oauth/google")
    val url: String,
)

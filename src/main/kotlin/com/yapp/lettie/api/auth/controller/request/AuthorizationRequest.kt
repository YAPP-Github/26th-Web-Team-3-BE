package com.yapp.lettie.api.auth.controller.request

import io.swagger.v3.oas.annotations.media.Schema

data class AuthorizationRequest(
    @Schema(description = "authorization code")
    val authorizationCode: String,
)

package com.yapp.lettie.api.timecapsule.service.dto

import org.springframework.data.domain.Pageable

data class SearchTimeCapsulesPayload(
    val keyword: String,
    val pageable: Pageable,
)

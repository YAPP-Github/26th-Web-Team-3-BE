package com.yapp.lettie.api.letter.service.dto

import org.springframework.data.domain.Pageable

data class GetLettersPayload(
    val capsuleId: Long,
    val pageable: Pageable,
)

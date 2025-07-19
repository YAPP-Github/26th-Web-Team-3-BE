package com.yapp.lettie.api.timecapsule.service.dto

import java.time.LocalDate

data class RemainingTimeDto(
    val days: Long? = null,
    val hours: Long? = null,
    val minutes: Long? = null,
    val openDate: LocalDate? = null,
)

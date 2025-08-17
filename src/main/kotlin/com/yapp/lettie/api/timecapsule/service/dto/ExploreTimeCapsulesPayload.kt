package com.yapp.lettie.api.timecapsule.service.dto

import com.yapp.lettie.domain.timecapsule.entity.vo.CapsuleSort
import com.yapp.lettie.domain.timecapsule.entity.vo.TimeCapsuleStatus
import org.springframework.data.domain.Pageable

data class ExploreTimeCapsulesPayload(
    val type: TimeCapsuleStatus?,
    val sort: CapsuleSort,
    val pageable: Pageable,
) {
    companion object {
        fun of(
            type: TimeCapsuleStatus?,
            sort: CapsuleSort,
            pageable: Pageable,
        ): ExploreTimeCapsulesPayload =
            ExploreTimeCapsulesPayload(
                type = type,
                sort = sort,
                pageable = pageable,
            )
    }
}

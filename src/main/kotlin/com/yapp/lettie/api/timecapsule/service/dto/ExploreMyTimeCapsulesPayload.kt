package com.yapp.lettie.api.timecapsule.service.dto

import com.yapp.lettie.domain.timecapsule.entity.vo.CapsuleSort
import com.yapp.lettie.domain.timecapsule.entity.vo.MyCapsuleFilter
import org.springframework.data.domain.Pageable

data class ExploreMyTimeCapsulesPayload(
    val filter: MyCapsuleFilter,
    val sort: CapsuleSort,
    val pageable: Pageable,
) {
    companion object {
        fun of(
            filter: MyCapsuleFilter,
            sort: CapsuleSort,
            pageable: Pageable,
        ): ExploreMyTimeCapsulesPayload =
            ExploreMyTimeCapsulesPayload(
                filter = filter,
                sort = sort,
                pageable = pageable,
            )
    }
}

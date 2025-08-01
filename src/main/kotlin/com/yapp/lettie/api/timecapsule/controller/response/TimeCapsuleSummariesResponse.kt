package com.yapp.lettie.api.timecapsule.controller.response

import com.yapp.lettie.api.timecapsule.service.dto.TimeCapsuleSummariesDto

data class TimeCapsuleSummariesResponse(
    val timeCapsules: List<TimeCapsuleSummaryResponse>,
    val totalCount: Long,
    val totalPages: Int,
    val pageNumber: Int,
    val pageSize: Int,
) {
    companion object {
        fun from(timeCapsuleSummaries: TimeCapsuleSummariesDto): TimeCapsuleSummariesResponse =
            TimeCapsuleSummariesResponse(
                timeCapsules = timeCapsuleSummaries.timeCapsules.map { TimeCapsuleSummaryResponse.from(it) },
                totalCount = timeCapsuleSummaries.totalCount,
                totalPages = timeCapsuleSummaries.totalPages,
                pageNumber = timeCapsuleSummaries.page,
                pageSize = timeCapsuleSummaries.size,
            )
    }
}

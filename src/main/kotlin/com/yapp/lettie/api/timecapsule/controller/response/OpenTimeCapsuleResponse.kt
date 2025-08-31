package com.yapp.lettie.api.timecapsule.controller.response

import com.yapp.lettie.api.timecapsule.service.dto.OpenTimeCapsuleDto

data class OpenTimeCapsuleResponse(
    val isFirstOpen: Boolean,
) {
    companion object {
        fun from(dto: OpenTimeCapsuleDto): OpenTimeCapsuleResponse =
            OpenTimeCapsuleResponse(
                isFirstOpen = dto.isFirstOpen,
            )
    }
}

package com.yapp.lettie.api.timecapsule.service.dto

data class CreateTimeCapsuleDto(
    val id: Long,
    val inviteCode: String,
) {
    companion object {
        fun of(
            id: Long,
            inviteCode: String,
        ): CreateTimeCapsuleDto {
            return CreateTimeCapsuleDto(
                id = id,
                inviteCode = inviteCode,
            )
        }
    }
}

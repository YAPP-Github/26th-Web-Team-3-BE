package com.yapp.lettie.domain.timecapsule.repository

import com.yapp.lettie.domain.timecapsule.dto.RecipientRow

interface TimeCapsuleUserCustomRepository {
    fun findRecipientsByCapsuleIds(capsuleIds: List<Long>): List<RecipientRow>
}

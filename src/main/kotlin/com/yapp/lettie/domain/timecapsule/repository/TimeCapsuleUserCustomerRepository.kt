package com.yapp.lettie.domain.timecapsule.repository

import com.yapp.lettie.api.timecapsule.service.dto.RecipientRow

interface TimeCapsuleUserCustomerRepository {
    fun findRecipientsByCapsuleIds(capsuleIds: List<Long>): List<RecipientRow>
}

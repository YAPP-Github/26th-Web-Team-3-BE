package com.yapp.lettie.api.letter.service.reader

import com.yapp.lettie.domain.letter.repository.LetterRepository
import org.springframework.stereotype.Component

@Component
class LetterReader(
    private val letterRepository: LetterRepository,
) {
    fun getLetterCountByCapsuleId(capsuleId: Long): Int {
        return letterRepository.countByTimeCapsuleId(capsuleId)
    }
}

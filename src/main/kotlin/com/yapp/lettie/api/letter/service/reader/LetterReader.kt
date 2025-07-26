package com.yapp.lettie.api.letter.service.reader

import com.yapp.lettie.domain.letter.entity.Letter
import com.yapp.lettie.domain.letter.repository.LetterRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class LetterReader(
    private val letterRepository: LetterRepository,
) {
    fun findByCapsuleId(
        capsuleId: Long,
        pageable: Pageable,
    ): Page<Letter> = letterRepository.findByTimeCapsuleId(capsuleId, pageable)
}

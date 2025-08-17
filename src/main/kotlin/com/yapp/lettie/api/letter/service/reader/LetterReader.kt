package com.yapp.lettie.api.letter.service.reader

import com.yapp.lettie.common.error.ErrorMessages
import com.yapp.lettie.common.exception.ApiErrorException
import com.yapp.lettie.domain.letter.entity.Letter
import com.yapp.lettie.domain.letter.repository.LetterRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class LetterReader(
    private val letterRepository: LetterRepository,
) {
    @Transactional(readOnly = true)
    fun findByCapsuleId(
        capsuleId: Long,
        pageable: Pageable,
    ): Page<Letter> = letterRepository.findByTimeCapsuleId(capsuleId, pageable)

    @Transactional(readOnly = true)
    fun getById(letterId: Long): Letter =
        letterRepository.findByIdOrNull(letterId) ?: throw ApiErrorException(ErrorMessages.LETTER_NOT_FOUND)

    @Transactional(readOnly = true)
    fun getLetterCountByCapsuleId(capsuleId: Long): Int {
        return letterRepository.countByTimeCapsuleId(capsuleId)
    }

    @Transactional(readOnly = true)
    fun getLetterCountMap(capsuleIds: List<Long>): Map<Long, Int> {
        return letterRepository.getCountGroupedByCapsuleIds(capsuleIds)
            .associate { row ->
                val capsuleId = row[0] as Long
                val count = (row[1] as Long).toInt()
                capsuleId to count
            }
    }
}

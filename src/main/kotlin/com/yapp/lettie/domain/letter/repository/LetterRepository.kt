package com.yapp.lettie.domain.letter.repository

import com.yapp.lettie.domain.letter.entity.Letter
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface LetterRepository : JpaRepository<Letter, Long> {
    fun findByTimeCapsuleId(
        capsuleId: Long,
        pageable: Pageable,
    ): Page<Letter>
}

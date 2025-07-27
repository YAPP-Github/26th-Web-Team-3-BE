package com.yapp.lettie.domain.letter.repository

import com.yapp.lettie.domain.letter.entity.Letter
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface LetterRepository : JpaRepository<Letter, Long> {
    fun countByTimeCapsuleId(capsuleId: Long): Int

    fun findByTimeCapsuleId(
        capsuleId: Long,
        pageable: Pageable,
    ): Page<Letter>

    @Query(
        """
        SELECT l.timeCapsule.id, COUNT(l)
        FROM Letter l
        WHERE l.timeCapsule.id IN :capsuleIds
        GROUP BY l.timeCapsule.id
    """,
    )
    fun getCountGroupedByCapsuleIds(
        @Param("capsuleIds") capsuleIds: List<Long>,
    ): List<Array<Any>>
}

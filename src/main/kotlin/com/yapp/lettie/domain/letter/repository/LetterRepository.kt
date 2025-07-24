package com.yapp.lettie.domain.letter.repository

import com.yapp.lettie.domain.letter.entity.Letter
import org.springframework.data.jpa.repository.JpaRepository

interface LetterRepository : JpaRepository<Letter, Long>

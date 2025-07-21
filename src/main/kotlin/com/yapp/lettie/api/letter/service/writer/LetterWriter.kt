package com.yapp.lettie.api.letter.service.writer

import com.yapp.lettie.domain.letter.entity.Letter
import com.yapp.lettie.domain.letter.repository.LetterRepository
import org.springframework.stereotype.Service

@Service
class LetterWriter(
    private val letterRepository: LetterRepository,
) {
    fun save(letter: Letter): Letter = letterRepository.save(letter)
}

package com.yapp.lettie.api.file.service.writer

import com.yapp.lettie.domain.file.entity.LetterFile
import com.yapp.lettie.domain.file.repository.FileRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FileWriter(
    private val fileRepository: FileRepository,
) {
    @Transactional
    fun save(letterFile: LetterFile): LetterFile = fileRepository.save(letterFile)
}

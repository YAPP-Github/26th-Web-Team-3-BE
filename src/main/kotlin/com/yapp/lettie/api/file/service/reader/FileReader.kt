package com.yapp.lettie.api.file.service.reader

import com.yapp.lettie.common.error.ErrorMessages
import com.yapp.lettie.common.exception.ApiErrorException
import com.yapp.lettie.domain.file.repository.FileRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class FileReader(
    private val fileRepository: FileRepository,
) {
    fun getById(id: Long) = fileRepository.findByIdOrNull(id) ?: throw ApiErrorException(ErrorMessages.FILE_NOT_FOUND)
}

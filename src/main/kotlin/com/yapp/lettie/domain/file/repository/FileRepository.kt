package com.yapp.lettie.domain.file.repository

import com.yapp.lettie.domain.file.entity.LetterFile
import org.springframework.data.jpa.repository.JpaRepository

interface FileRepository : JpaRepository<LetterFile, Long>

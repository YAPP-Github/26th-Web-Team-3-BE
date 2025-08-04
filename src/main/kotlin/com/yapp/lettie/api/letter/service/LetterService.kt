package com.yapp.lettie.api.letter.service

import com.yapp.lettie.api.file.service.writer.FileWriter
import com.yapp.lettie.api.letter.service.dto.CreateLetterPayload
import com.yapp.lettie.api.letter.service.dto.GetLettersPayload
import com.yapp.lettie.api.letter.service.dto.LetterDto
import com.yapp.lettie.api.letter.service.dto.LettersDto
import com.yapp.lettie.api.letter.service.reader.LetterReader
import com.yapp.lettie.api.letter.service.writer.LetterWriter
import com.yapp.lettie.api.timecapsule.service.reader.TimeCapsuleReader
import com.yapp.lettie.api.timecapsule.service.reader.TimeCapsuleUserReader
import com.yapp.lettie.api.user.service.reader.UserReader
import com.yapp.lettie.common.dto.UserInfoPayload
import com.yapp.lettie.common.error.ErrorMessages
import com.yapp.lettie.common.exception.ApiErrorException
import com.yapp.lettie.domain.file.entity.LetterFile
import com.yapp.lettie.domain.letter.entity.Letter
import com.yapp.lettie.domain.timecapsule.entity.TimeCapsuleUser
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class LetterService(
    private val timeCapsuleReader: TimeCapsuleReader,
    private val timeCapsuleUserReader: TimeCapsuleUserReader,
    private val userReader: UserReader,
    private val letterWriter: LetterWriter,
    private val letterReader: LetterReader,
    private val fileWriter: FileWriter,
) {
    @Transactional
    fun writeLetter(
        userId: Long,
        payload: CreateLetterPayload,
    ): Long {
        val capsule = timeCapsuleReader.getById(payload.capsuleId)
        val user = userReader.getById(userId)

        if (capsule.isClosed(LocalDateTime.now())) {
            throw ApiErrorException(ErrorMessages.CLOSED_TIME_CAPSULE)
        }

        val alreadyJoined = timeCapsuleUserReader.hasUserJoinedCapsule(user.id, capsule.id)
        if (!alreadyJoined) {
            val timeCapsuleUser = TimeCapsuleUser.of(user, capsule)
            capsule.addUser(timeCapsuleUser)
            user.addTimeCapsuleUser(timeCapsuleUser)
        }

        val letter =
            letterWriter.save(
                Letter.of(
                    user = user,
                    timeCapsule = capsule,
                    content = payload.content,
                    from = payload.from,
                ),
            )

        payload.objectKey?.let { objectKey ->
            val file =
                fileWriter.save(
                    LetterFile.of(
                        objectKey = objectKey,
                        letter = letter,
                    ),
                )
            letter.addFile(file)
        }

        return letter.id
    }

    @Transactional(readOnly = true)
    fun readLetters(
        user: UserInfoPayload,
        payload: GetLettersPayload,
    ): LettersDto {
        val capsule = timeCapsuleReader.getById(payload.capsuleId)

        validateTimeCapsuleRead(capsule.id, user.id)

        val letters = letterReader.findByCapsuleId(payload.capsuleId, payload.pageable)
        return LettersDto.of(user.id, letters)
    }

    @Transactional(readOnly = true)
    fun readLetter(
        user: UserInfoPayload,
        letterId: Long,
    ): LetterDto {
        val letter = letterReader.getById(letterId)
        validateTimeCapsuleRead(letter.timeCapsule.id, user.id)

        return LetterDto.of(user.id, letter)
    }

    private fun validateTimeCapsuleRead(
        capsuleId: Long,
        userId: Long,
    ) {
        val capsule = timeCapsuleReader.getById(capsuleId)

        if (capsule.isNotOpen(LocalDateTime.now())) {
            throw ApiErrorException(ErrorMessages.NOT_OPENED_CAPSULE)
        }

        if (capsule.isPrivate() && capsule.timeCapsuleUsers.none { it.user.id == userId }) {
            throw ApiErrorException(ErrorMessages.NOT_JOINED_TIME_CAPSULE)
        }
    }
}

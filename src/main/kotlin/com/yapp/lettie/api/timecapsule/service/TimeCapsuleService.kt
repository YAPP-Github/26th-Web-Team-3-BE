package com.yapp.lettie.api.timecapsule.service

import com.yapp.lettie.api.timecapsule.service.dto.CreateTimeCapsulePayload
import com.yapp.lettie.api.timecapsule.service.reader.TimeCapsuleReader
import com.yapp.lettie.api.timecapsule.service.writer.TimeCapsuleWriter
import com.yapp.lettie.api.user.service.reader.UserReader
import com.yapp.lettie.common.error.ErrorMessages
import com.yapp.lettie.common.exception.ApiErrorException
import com.yapp.lettie.domain.timecapsule.entity.TimeCapsule
import com.yapp.lettie.domain.timecapsule.entity.TimeCapsuleUser
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.UUID

@Service
class TimeCapsuleService(
    private val userReader: UserReader,
    private val timeCapsuleWriter: TimeCapsuleWriter,
    private val timeCapsuleReader: TimeCapsuleReader,
) {
    @Transactional
    fun createTimeCapsule(
        userId: Long,
        payload: CreateTimeCapsulePayload,
    ) {
        val user = userReader.getById(userId)
        val capsule = TimeCapsule.of(generateInviteCode(), payload)
        val timeCapsuleUser = TimeCapsuleUser.of(user, capsule)

        capsule.addUser(timeCapsuleUser)
        user.addTimeCapsuleUser(timeCapsuleUser)

        timeCapsuleWriter.save(capsule)
    }

    @Transactional
    fun joinTimeCapsule(
        userId: Long,
        capsuleId: Long,
    ) {
        val capsule = timeCapsuleReader.getById(capsuleId)
        val user = userReader.getById(userId)

        if (capsule.closedAt.isBefore(LocalDate.now())) {
            throw ApiErrorException(ErrorMessages.CLOSED_TIME_CAPSULE)
        }

        if (capsule.timeCapsuleUsers.any { it.user.id == user.id }) {
            throw ApiErrorException(ErrorMessages.ALREADY_JOINED)
        }

        val timeCapsuleUser = TimeCapsuleUser.of(user, capsule)
        capsule.addUser(timeCapsuleUser)
        user.addTimeCapsuleUser(timeCapsuleUser)
    }

    private fun generateInviteCode(): String {
        return UUID.randomUUID().toString().take(RANDOM_VALUE_LENGTH)
    }

    companion object {
        private const val RANDOM_VALUE_LENGTH = 8
    }
}

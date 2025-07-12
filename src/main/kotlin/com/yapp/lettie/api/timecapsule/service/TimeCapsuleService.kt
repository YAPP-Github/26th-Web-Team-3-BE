package com.yapp.lettie.api.timecapsule.service

import com.yapp.lettie.api.timecapsule.service.dto.CreateTimeCapsulePayload
import com.yapp.lettie.api.timecapsule.service.writer.TimeCapsuleWriter
import com.yapp.lettie.api.user.service.reader.UserReader
import com.yapp.lettie.domain.timecapsule.entity.TimeCapsule
import com.yapp.lettie.domain.timecapsule.entity.TimeCapsuleUser
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class TimeCapsuleService(
    private val userReader: UserReader,
    private val timeCapsuleWriter: TimeCapsuleWriter,
) {
    fun createTimeCapsule(
        userId: Long,
        payload: CreateTimeCapsulePayload,
    ) {
        val capsule = TimeCapsule.of(generateInviteCode(), payload)
        val timeCapsuleUser = TimeCapsuleUser.of(userReader.getById(userId), capsule)
        capsule.timeCapsuleUsers.add(timeCapsuleUser)

        timeCapsuleWriter.save(capsule)
    }

    private fun generateInviteCode(): String {
        return UUID.randomUUID().toString().take(RANDOM_VALUE_LENGTH)
    }

    companion object {
        private const val RANDOM_VALUE_LENGTH = 8
    }
}
